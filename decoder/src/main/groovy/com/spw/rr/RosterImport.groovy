package com.spw.rr

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
        def newList = database.listDecoders()
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
        def firstEntry = rosterValues.roster.locomotive[0]
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
                if (previous == null) {
                    addLoco(newEntry)
                } else {
                    newEntry.id = previous.id
                    database.updateDecoderEntry(newEntry )
                    existingList.remove(newEntry.decoderId)
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
