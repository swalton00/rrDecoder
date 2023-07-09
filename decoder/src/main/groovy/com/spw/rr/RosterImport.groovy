package com.spw.rr

import com.spw.rr.mappers.DecoderEntry
import com.spw.rr.mappers.DecoderType
import com.spw.rr.mappers.FunctionLabel
import com.spw.rr.mappers.KeyValuePairs
import com.spw.rr.mappers.RosterEntry
import com.spw.rr.mappers.SpeedProfile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.fasterxml.jackson.databind.util.StdDateFormat

import java.sql.Timestamp
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

class RosterImport {
    static final Logger log = LoggerFactory.getLogger(RosterImport.class)

    private DecoderDBService database

    List<DecoderType> decoderList = null

    void setDB(DecoderDBService database) {
        log.debug("setting the database service address")
        this.database = database
    }

    private static String getSystemName() {
        log.debug("getting System name")
        return System.getenv("COMPUTERNAME")
    }


    void buildDecoderList() {
        log.debug("now building a list of current decoders")
        if (decoderList == null) {
            decoderList = new ArrayList<DecoderType>()
        } else {
            decoderList.clear()
        }
        def newList = database.listDecoderTypes()
        newList.each { entry ->
            decoderList.add(entry)
        }
    }

    DecoderType findDecoderType(String family, String model) {
        log.debug("finding decoder with family: ${family} and model: ${model}")
        DecoderType found = null
        decoderList.each {
            if (it.decoderFamily.equals(family) && it.decoderModel.equals(model)) {
                log.debug("found the decoder")
                found = it
                return
            }
        }
        if (found != null) {
            return found
        }
        found = new DecoderType()
        found.decoderModel = model
        found.decoderFamily = family
        found = database.insertDecoderTypeEntry(found)
        decoderList.add(found)
        return found
    }

    boolean doesRosterExist(File rosterFile) {
        log.debug("looking for an existing roster on this system for ${rosterFile}")
        RosterEntry existing = getRosterEntry(rosterFile.path)
        if (existing != null) {
            log.debug("found an existing roster -- returning true")
            return true
        } else {
            log.debug("no roster found -- returning false")
            return false
        }
    }

    RosterEntry importRoster(File rosterFile) {
        log.debug("importing from ${rosterFile.path}")
        String rosterText = rosterFile.text
        def rosterValues = new XmlSlurper().parseText(rosterText)
        int arraySize = rosterValues.roster.locomotive.size()
        def entryList = rosterValues.roster.locomotive
        buildDecoderList()
        RosterEntry thisRoster = getRosterEntry(rosterFile.path)
        boolean rosterFound = false
        HashMap<String, DecoderEntry> existingList = null
        if (thisRoster == null) {
            log.debug("roster not found -- adding new")
            thisRoster = new RosterEntry()
            thisRoster.fullPath = rosterFile.path
            thisRoster.systemName = getSystemName()
            thisRoster = database.addRoster(thisRoster)
        } else {
            rosterFound = true
            existingList = updateRosterEntries(thisRoster)
        }
        for (i in 0..<arraySize) {
            log.debug("this entry has an id of ${entryList[i].'@id'.text()}")
            DecoderEntry newEntry = setLocoValues(entryList[i], thisRoster)
            if (!rosterFound) {
                addLoco(newEntry)
            } else {
                DecoderEntry previous = existingList.get(newEntry.decoderId)
                if (previous != null) {
                    // delete previous to cascade delete any children
                    database.deleteDecoderEntry(previous)
                    existingList.remove(newEntry.decoderId)
                }
                addLoco(newEntry)
            }
            // check for additional information in the roster - function labels, attribute pairs and speed profile
            def functions = entryList[i].'functionLabels'
            def functionEntries = functions.'functionlabel'
            if (functionEntries != null) {
                int functionLabelSize = entryList[i].'functionlabels'.functionlabel.size()
                for (labelEntry in 0..<functionLabelSize) {
                    log.debug("this function label entry has ${entryList[i].'functionlabels'.functionlabel[labelEntry].'@num'.text()} and ${entryList[i].'functionlabels'.functionlabel[labelEntry].text()}")
                    FunctionLabel funcLab = new FunctionLabel()
                    funcLab.decoderId = newEntry.id
                    funcLab.functionNum = Integer.valueOf(entryList[i].'functionlabels'.functionlabel[labelEntry].'@num'.text())
                    funcLab.functionLabel = entryList[i].'functionlabels'.functionlabel[labelEntry].text()
                    log.debug("new function label is ${funcLab}")
                }
            }
            int keyValuesSize = entryList[i].attributepairs.keyvaluepair.size()
            log.debug("key value size is ${keyValuesSize}")
            if (keyValuesSize > 0) {
                for (j in 0..<keyValuesSize) {
                    KeyValuePairs kvp = new KeyValuePairs()
                    kvp.decoderId = newEntry.id
                    kvp.pair_key = entryList[i].attributepairs.keyvaluepair[j].'key'.text()
                    kvp.pair_value = entryList[i].attributepairs.keyvaluepair[j].'value'.text()
                    log.debug("new key value pair is: ${kvp}")
                }
            }
            int speedProfileSize = entryList[i].'speedprofile'.speeds.speed.size()
            log.debug("speed profile size is ${speedProfileSize}")
            if (speedProfileSize > 0) {
                for (j in 0..<speedProfileSize) {
                    SpeedProfile sp = new SpeedProfile()
                    sp.decoderId = newEntry.id
                    sp.speedStep = Integer.valueOf(entryList[i].'speedprofile'.speeds.speed[j].step.text())
                    sp.forwardValue = Double.valueOf(entryList[i].'speedprofile'.speeds.speed[j].forward.text())
                    sp.reverseValue = Double.valueOf(entryList[i].'speedprofile'.speeds.speed[j].reverse.text())
                    log.debug("new speed profile is ${sp}")
                }
            }
        }
        if (rosterFound && existingList.size() > 0) {
            log.debug("still have some old existing decoder entries -- removing them")
            existingList.forEach {
                database.deleteDecoderEntry(it)
            }
        }
        log.debug("there are ${arraySize} entries in this roster")
        if (rosterFound) {
            database.updateRosterEntry(thisRoster)
        }
        return thisRoster
    }

    DecoderEntry addLoco(DecoderEntry entry) {
        database.addDecoderEntry(entry)
    }

    DecoderEntry setLocoValues(Object thisEntry, RosterEntry rosterEntry) {
        DecoderEntry entry = new DecoderEntry()
        entry.rosterId = rosterEntry.id
        String decoderModel = thisEntry.decoder.'@model'
        String decoderFamily = thisEntry.decoder.'@family'
        log.debug("find decoder type  for ${decoderModel} with family ${decoderFamily}")
        DecoderType decoderType = findDecoderType(decoderFamily, decoderModel)
        entry.decoderTypeId = decoderType.id
        entry.decoderId = thisEntry.'@id'
        entry.fileName = thisEntry.'@fileName'
        entry.roadName = thisEntry.'@roadName'
        entry.roadNumber = thisEntry.'@roadNumber'
        entry.manufacturer = thisEntry.'@mfg'
        entry.owner = thisEntry.'@owner'
        entry.model = thisEntry.'@model'
        entry.dccAddress = thisEntry.'@dccAddress'
        entry.manufacturerId = thisEntry.'@manufacturerID'
        entry.productId = thisEntry.'@productID'
        log.debug("date from XML was ${thisEntry.'dateUpdated'.text()}")
        entry.dateUpdated = doDateModified(thisEntry.'dateUpdated'.text())
        log.debug("dateupdated set to ${entry.dateUpdated}")
        return entry
    }

    Timestamp doDateModified(String dateValue) {
        Timestamp retVal = null
        if (dateValue == null || dateValue.equals("")) {
            return new Timestamp(new Date().getTime())
        }
        try {
            return new Timestamp(new StdDateFormat().parse(dateValue).getTime())
        } catch (ParseException ex) {
            log.debug("data parse exception -- trying SimpleDateFormat")
            try {

                return new Timestamp(DateFormat.getTimeInstance().parse(dateValue).getTime())
                return retVal
            } catch (ParseException ex2) {
                log.debug("that didn't work -- trying custom format")
                DateFormat customFmt = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
                try {
                    return new Timestamp(customFmt.parse(dateValue).getTime())
                } catch (ParseException ex3) {
// then try with a specific format to handle e.g. "01-Oct-2016 9:13:36"
                    customFmt = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
                    return new Timestamp(customFmt.parse(dateValue))
                }
            } catch (IllegalArgumentException ex2) {
                log.error("Illegal argument for DateUpdated -- setting to current date")
            }
        }
        return new Timestamp(new Date().getTime())
    }

    HashMap<String, DecoderEntry> updateRosterEntries(RosterEntry thisEntry) {
        log.debug("updating an existing roster")
        List<DecoderEntry> existingList = database.decodersForRoster(thisEntry.id)
        HashMap<String, DecoderEntry> oldLocos = new HashMap<>()
        existingList.each {
            oldLocos.put(it.decoderId, it)
        }
        return oldLocos
    }

    /*
    decoder processing
    1 - check for existing roster entry
        a update if present
        b add if not
    2 - process the locomotives
        a check for decoder
            get decoderId
            add if not present
        b populate all fields
        add
    if updating
        retrieve locomotive entry
        update values
     */

    RosterEntry getRosterEntry(String fullPath) {
        String systemName = getSystemName()
        return database.getRosterEntry(systemName, fullPath)
    }
}
