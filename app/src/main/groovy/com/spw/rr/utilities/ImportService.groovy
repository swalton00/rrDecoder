package com.spw.rr.utilities

import com.spw.rr.database.*
import groovy.xml.XmlSlurper
import org.perf4j.log4j.Log4JStopWatch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import java.awt.*
import java.sql.Timestamp
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.List


@Singleton
class ImportService {
    DatabaseServices database = DatabaseServices.getInstance()
    private static final Logger log = LoggerFactory.getLogger(com.spw.rr.utilities.ImportService.class)
    Component parent
    List<DecoderType> decoderList = null

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

    void setParent(Component parent) {
        this.parent = parent
    }

    RosterEntry getRosterEntry(String fullPath) {
        String systemName = getSystemName()
        return database.getRosterEntry(systemName, fullPath)
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
        Log4JStopWatch decoderListTime = new Log4JStopWatch("decoderList", "listing the decoders")
        def newList = database.listDecoderTypes()
        newList.each { entry ->
            decoderList.add(entry)
        }
        decoderListTime.stop()
    }


    RosterEntry importRoster(File rosterFile) {
        log.debug("importing from ${rosterFile.path}")
        String rosterText = rosterFile.text
        def rosterValues = new XmlSlurper().parseText(rosterText)
        int arraySize = rosterValues.roster.locomotive.size()
        def entryList = rosterValues.roster.locomotive
        ProgressMonitor monitor = new ProgressMonitor(parent, "Importing Decoders", "Reading XML", 0, 1)
        monitor.setMillisToDecideToPopup(10)
        buildDecoderList()
        RosterEntry thisEntry = getRosterEntry(rosterFile.path)
        boolean rosterFound = false
        HashMap<String, DecoderEntry> existingList = null
        Log4JStopWatch importTime = new Log4JStopWatch("import", "Starting the import")
        try {
            database.beginTransaction()
            if (thisEntry == null) {
                log.debug("roster not found -- adding new")
                thisEntry = new RosterEntry()
                thisEntry.fullPath = rosterFile.path
                thisEntry.systemName = getSystemName()
                thisEntry = database.addRoster(thisEntry)
                log.debug("this entry is now ${thisEntry}")
            } else {
                rosterFound = true
                existingList = updateRosterEntries(thisEntry)
            }
            SwingUtilities.invokeLater {
                monitor.setMaximum(arraySize)
                monitor.setNote("Importing entries")
            }
            Log4JStopWatch rosterStopWatch = new Log4JStopWatch("roster", "overall roster processing")
            for (i in 0..<arraySize) {
                SwingUtilities.invokeLater {
                    monitor.setProgress(i)
                }
                log.debug("this entry has an id of ${entryList[i].'@fileName'.text()}")
                Log4JStopWatch individualStopWatch = new Log4JStopWatch("indiv", "each roster entry${entryList[i].'@id'.text()}")
                DecoderEntry newEntry = new DecoderEntry()
                boolean decoderExists = false
                if (rosterFound) {
                    DecoderEntry previous = existingList.get(entryList[i].'@fileName')
                    if (previous != null) {
                        newEntry = previous
                        decoderExists = true
                        existingList.remove(newEntry.fileName)
                    }
                }
                if (!rosterFound | (rosterFound & !decoderExists)) {
                    log.debug("no database entry found -- inserting")
                    setLocoValues(newEntry, entryList[i], thisEntry)
                    database.addDecoderEntry(newEntry)
                } else {
                    log.debug("existing entry being updated id = ${newEntry.id}")
                    setLocoValues(newEntry, entryList[i], thisEntry)
                    database.updateDecoderEntry(newEntry)
                }
                individualStopWatch.stop()
                // check for additional information in the roster - function labels, attribute pairs and speed profile
                def functions = entryList[i].'functionLabels'
                def functionEntries = functions.'functionlabel'
                if (functionEntries != null) {
                    Log4JStopWatch functionsStopWatch = new Log4JStopWatch("functions", "function entries")
                    int functionLabelSize = entryList[i].'functionlabels'.functionlabel.size()
                    for (labelEntry in 0..<functionLabelSize) {
                        log.debug("this function label entry has ${entryList[i].'functionlabels'.functionlabel[labelEntry].'@num'.text()} and ${entryList[i].'functionlabels'.functionlabel[labelEntry].text()}")
                        FunctionLabel funcLab = new FunctionLabel()
                        funcLab.decoderId = newEntry.id
                        funcLab.functionNum = Integer.valueOf(entryList[i].'functionlabels'.functionlabel[labelEntry].'@num'.text())
                        funcLab.functionLabel = entryList[i].'functionlabels'.functionlabel[labelEntry].text()
                        log.debug("new function label is ${funcLab}")
                        database.insertFunctionLabel(funcLab)
                    }
                    functionsStopWatch.stop()
                }
                int keyValuesSize = entryList[i].attributepairs.keyvaluepair.size()
                log.debug("key value size is ${keyValuesSize}")
                if (keyValuesSize > 0) {
                    Log4JStopWatch keyValsStopWatch = new Log4JStopWatch("kvp", "key value pairs")
                    for (j in 0..<keyValuesSize) {
                        KeyValuePairs kvp = new KeyValuePairs()
                        kvp.decoderId = newEntry.id
                        kvp.pair_key = entryList[i].attributepairs.keyvaluepair[j].'key'.text()
                        kvp.pair_value = entryList[i].attributepairs.keyvaluepair[j].'value'.text()
                        log.debug("new key value pair is: ${kvp}")
                        database.insertKeyValuePair(kvp)
                    }
                    keyValsStopWatch.stop()
                }
                int speedProfileSize = entryList[i].'speedprofile'.speeds.speed.size()
                log.debug("speed profile size is ${speedProfileSize}")
                if (speedProfileSize > 0) {
                    Log4JStopWatch speedStopWatch = new Log4JStopWatch("speeds", "Speed Profile")
                    for (j in 0..<speedProfileSize) {
                        SpeedProfile sp = new SpeedProfile()
                        sp.decoderId = newEntry.id
                        sp.speedStep = Integer.valueOf(entryList[i].'speedprofile'.speeds.speed[j].step.text())
                        sp.forwardValue = Double.valueOf(entryList[i].'speedprofile'.speeds.speed[j].forward.text())
                        sp.reverseValue = Double.valueOf(entryList[i].'speedprofile'.speeds.speed[j].reverse.text())
                        log.debug("new speed profile is ${sp}")
                        database.insertSpeedProfile(sp)
                    }
                    speedStopWatch.stop()
                }
            }
            if (rosterFound && existingList.size() > 0) {
                log.debug("still have some old existing decoder entries -- removing them -- ${existingList.size()}")
                existingList.each {
                    database.deleteDecoderEntry(it)
                }
            }
            if (rosterFound) {
                database.updateRosterEntry(thisEntry)
            }
            database.commitWork()
        } catch (Exception e) {
            log.error("Caught an exception working with the import", e)
            database.rollbackAll()
        } finally {
            log.trace("closing the progress monitor")
            monitor.setProgress(arraySize)
            monitor.close()
            database.close()  // free up the session
            importTime.stop()
        }
        log.debug("there are ${arraySize} entries in this roster")
        thisEntry.decCount = arraySize
        return thisEntry
    }

    HashMap<String, DecoderEntry> updateRosterEntries(RosterEntry thisEntry) {
        log.debug("updating an existing roster")
        List<DecoderEntry> existingList = database.decodersForRoster(thisEntry.id)
        HashMap<String, DecoderEntry> oldLocos = new HashMap<>()
        existingList.each {
            oldLocos.put(it.fileName, it)
        }
        return oldLocos
    }


    DecoderEntry setLocoValues(DecoderEntry entry, Object thisEntry, RosterEntry rosterEntry) {
        entry.rosterId = rosterEntry.id
        entry.decoderId = thisEntry.'@id'
        String decoderModel = thisEntry.decoder.'@model'
        String decoderFamily = thisEntry.decoder.'@family'
        log.debug("find decoder type  for ${decoderModel} with family ${decoderFamily}")
        DecoderType decoderType = findDecoderType(decoderFamily, decoderModel)
        entry.decoderTypeId = decoderType.id
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


    DecoderType findDecoderType(String family, String model) {
        log.debug("finding decoder with family: ${family} and model: ${model}")
        DecoderType found = null
        decoderList.each {
            if (it.decoderFamily.equals(family) && it.decoderModel.equals(model)) {
                log.debug("found the decoder")
                found = it
                return it
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

    Timestamp doDateModified(String dateValue) {
        Timestamp retVal = null
        if (dateValue == null || dateValue.equals("")) {
            return new Timestamp(new Date().getTime())
        }
        try {
            return new Timestamp(new Date().getTime())
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
}
