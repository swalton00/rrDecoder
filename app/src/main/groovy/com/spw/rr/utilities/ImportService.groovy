package com.spw.rr.utilities

import com.spw.rr.controllers.SeeProgressController
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
import java.util.concurrent.Semaphore


@Singleton
class ImportService {
    DatabaseServices database = DatabaseServices.getInstance()
    private static final Logger log = LoggerFactory.getLogger(ImportService.class)
    private static Semaphore importLock = new Semaphore(1)
    private static Semaphore detailLock = new Semaphore(1)
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


    RosterEntry importRoster(Component parent, File rosterFile) {
        log.debug("importing from ${rosterFile.path} - getting the lock")
        if (!importLock.tryAcquire()) {
            throw new RuntimeException("attempting to import a file while an import is in progress")
        }
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
            SwingUtilities.invokeLater {
                monitor.setProgress(arraySize)
                monitor.close()
            }
            database.close()  // free up the session
            importTime.stop()
        }
        log.debug("there are ${arraySize} entries in this roster - releasing the lock")
        thisEntry.decCount = arraySize
        importLock.release()
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

    void importDetail(Component parent, List<Integer> decoders) {
        log.debug("importing details for ${decoders.size()} decoders")
        if (detailLock.tryAcquire()) {
            log.debug("lock acquired XXXXX")
        } else {
            log.error("second import requested")
            throw new RuntimeException("attempt to run a second import")
        }
        HashMap<Integer, RosterEntry> rosterEntries = new HashMap<>()
        HashMap<Integer, String> rosterFiles = new HashMap<>()
        SeeProgressController monitor = new SeeProgressController(parent)
        monitor.setMainOverall(0, decoders.size())
        Log4JStopWatch detailStopWatch = new Log4JStopWatch("detail", "Importing details for ${decoders.size()}")
        int entryCounter = 0
        decoders.each { Integer decoderId ->
            entryCounter++
            monitor.setMainProgress(entryCounter, "Decoder ${entryCounter} of ${decoders.size()}")
            log.debug("processing details for decoder id of ${decoderId}")
            Log4JStopWatch decoderDetail = new Log4JStopWatch("decoderDetail", "processing decoder id of ${decoderId}")
            monitor.setIntermediateOverall(1, 5, "Read Decoder Entry", "Step 1 of 5")
            DecoderEntry decoderEntry = database.getDecoderEntry(decoderId)
            RosterEntry thisEntry = null
            if (rosterEntries.containsKey(decoderEntry.rosterId)) {
                thisEntry = rosterEntries.get(decoderEntry.rosterId)
            } else {
                thisEntry = database.getRosterEntry(decoderEntry.rosterId)
                rosterEntries.put(thisEntry.id, thisEntry)
                String path = thisEntry.fullPath.substring(0, thisEntry.fullPath.lastIndexOf(File.separator))
                rosterFiles.put(thisEntry.id, path)
            }
            monitor.setIntermediateProgress(2, "Read XML File", "Step 2 of 5")
            String decoderFileName = rosterFiles.get(decoderEntry.rosterId) +
                    File.separator + "roster" + File.separator + decoderEntry.fileName
            boolean fileFound = false
            String decoderText
            try {
                File xmlDecoderFile = new File(decoderFileName)
                decoderText = xmlDecoderFile.text
                fileFound = true
            } catch (FileNotFoundException fe) {
                log.error "File ${decoderFileName} was not found"
            }
            if (fileFound) {
                monitor.setIntermediateProgress(3, "Parse XML File", "Setep 3 of 5")
                log.debug("found roster xml for id ${decoderEntry.id}")
                groovy.util.XmlSlurper slurper = new groovy.util.XmlSlurper()
                slurper.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
                slurper.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
                def decoderXML = slurper.parseText(decoderText)
                try {
                    Log4JStopWatch individualStopWatch = new Log4JStopWatch("details1", "decoder id = ${decoderId}")
                    database.beginTransaction()
                    int varSize = decoderXML.'locomotive'.'values'.'decoderDef'.'varValue'.size()
                    // clean out any old CV values and DecoderDef rows first

                    database.prepareDetail(decoderEntry.id)
                    monitor.setIntermediateProgress(4, "Add Decoder Definition records", "Step 4 of 5")
                    monitor.setDetailOverall(0, varSize)

                    log.debug("decoderDef size is ${varSize}")
                    for (j in 0..<varSize) {
                        monitor.setDetailProgress(j, "Step ${j} of ${varSize}")
                        String itemString = decoderXML.'locomotive'.'values'.'decoderDef'.'varValue'[j].'@item'
                        String valueString = decoderXML.'locomotive'.'values'.'decoderDef'.'varValue'[j].'@value'
                        log.debug("value is ${valueString} and item is ${itemString}")
                        DecoderDef decoderDef = new DecoderDef()
                        decoderDef.parent = decoderId
                        decoderDef.item = itemString
                        decoderDef.varValue = valueString
                        database.insertDecoderDef(decoderDef)
                    }
                    int cvSize = decoderXML.'locomotive'.'values'.'CVvalue'.size()
                    monitor.setIntermediateProgress(5, "Add CV records", "Step 5 of 5")
                    log.debug("CV size is ${cvSize}")
                    monitor.setDetailOverall(0, cvSize)
                    for (j in 0..<cvSize) {
                        monitor.setDetailProgress(j, "${j} of ${cvSize}")
                        String name = decoderXML.'locomotive'.'values'.'CVvalue'[j].'@name'
                        String cvValue = decoderXML.'locomotive'.'values'.'CVvalue'[j].'@value'
                        log.debug("adding a CV number ${name} with value ${cvValue}")
                        CVvalues cVvalues = new CVvalues()
                        cVvalues.cvNumber = name
                        cVvalues.cvValue = cvValue
                        cVvalues.decoderId = decoderId
                        database.insertCVs(cVvalues)
                    }
                    database.updateDetailTime(decoderId)
                    database.commitWork()
                    log.trace("work now committed")
                    individualStopWatch.stop()
                } catch (Exception dbEx) {
                    log.error("exception processing the data -- rolling back", dbEx)
                    database.rollbackAll()
                }
            }
        }
        log.debug("lock released - YYYY")
        detailLock.release()
        monitor.view.setComplete()
        detailStopWatch.stop()
        log.debug("detail import complete")
    }

    void importDetailRoster(Component parent, ArrayList<Integer> rosterNumbers) {
        log.debug("Importing details for ${rosterNumbers.size()} roster entries")
        List<DecoderEntry> rosterEntries = database.decodersForRosterList(rosterNumbers)
        ArrayList<Integer> decoderList = new ArrayList<>()
        rosterEntries.each {
            decoderList.add(it.id)
        }
        importDetail(parent, decoderList)
    }

}
