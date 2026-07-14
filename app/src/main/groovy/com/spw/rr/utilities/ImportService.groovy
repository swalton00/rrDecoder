package com.spw.rr.utilities

import com.spw.rr.controllers.SeeProgressController
import com.spw.rr.database.*
import com.spw.rr.database.VersionBase.WhichTable
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
    ImportDb importDb = ImportDb.getInstance()
    private static final Logger log = LoggerFactory.getLogger(ImportService.class)
    private static Semaphore importLock = new Semaphore(1)
    private static Semaphore detailLock = new Semaphore(1)
    Component parent
    List<DecoderType> decoderList = null
    Timestamp dbTime = null

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

    static String getSystemName() {
        log.debug("getting System name")
        String systemName = System.getenv("COMPUTERNAME")
        log.debug("System name found was ${systemName}")
        if (systemName != null) {
            return systemName
        }
        try {
            systemName = InetAddress.getLocalHost().getHostName()
        } catch (UnknownHostException e) {
            log.error("Unknown hostname", e)
            systemName = "*Unknown*"
        }
        return systemName
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

    void importKeyDefs(def entryList, int decoderId, DecoderEntry decoderEntry) {
        log.debug("Processing the Key Value pairs for ${decoderEntry}")
        def entry = entryList.keyvaluepair
        int keyPairSize = entry.size()
        log.debug("size of keyValue pair list is ${keyPairSize}")
        VersionBase version = diffPhaseOne(decoderId, WhichTable.KEYVALUE)
        boolean newKeyDefs = false
        Hashtable<String, AbstractItem> existingHash = diffPhaseTwo(decoderId, WhichTable.KEYVALUE)
        if (existingHash == null) {
            newKeyDefs = true
        }
        for (index in 0..<keyPairSize) {
            log.debug("KVP Entry index is ${index} and this entry is ${entry[index].'key'.text()}")
            KeyValuePairs kvp = new KeyValuePairs()
            kvp.decoderId = decoderId
            kvp.pair_key = entry[index].'key'.text()
            kvp.pair_value = entry[index].'value'.text()
            log.debug("new keyValuePair is ${kvp}")
            log.debug("new KVP is Decoder: ${kvp.decoderId} , pairKey: ${kvp.pair_key} and value: ${kvp.pair_value}")
            if (newKeyDefs) {
                database.insertKeyValuePair(kvp)
            } else {
                KeyDiff diff = new KeyDiff()
                diff.pairKey = kvp.pair_key
                diff.newValue = kvp.pair_value
                log.debug("now going to processDiff")
                boolean useUpdate = processDiff(existingHash, kvp, version, diff)
                if (diff.wasChanged()) {
                    log.debug("kvp was changed ${diff}")

                    database.insertKeyValuePair(kvp, useUpdate)
                    database.insertKeyDiff(diff)
                }
            }
        }

        if (existingHash && existingHash.size() > 0) {
            log.debug("have some KeyValues to remove - ${existingHash.size()}")
           // ArrayList<String> deleteList = new ArrayList()
            if (!version.hasBeenWritten) {
                log.debug("Version hasn't been written yet - write it")
                database.insertVersion(version)
                version.hasBeenWritten = true
            }
            diffCleanUp(existingHash,
                decoderId,
                WhichTable.KEYVALUE,
                    version,
                    {AbstractItem oldItem ->
                        AbstractDiff newDiff = new KeyDiff()
                        log.debug("in the closure - new diff is ${newDiff}")
                        return newDiff
                    })
        }
    }

    VersionBase diffPhaseOne(int decoderId, WhichTable tableType) {
        log.debug("processing diff phase one for decoder ${decoderId}")
        VersionBase version = database.getLastVersion(decoderId, tableType)
        if (version == null) {
            version = new VersionBase(tableType)
            version.versionNumber = 1
            version.decoderId = decoderId
            version.tableSource = tableType
        } else {
            version.decoderId = decoderId
            version.tableSource = tableType
            version.versionNumber += 1
        }
        version.createdOn = dbTime
        log.debug("returning version ${version}")
        return version
    }

    Hashtable<String, AbstractItem> diffPhaseTwo(int decoderId, WhichTable tableType) {
        log.debug("getting existing entries for ${decoderId} of type ${tableType}")
        ArrayList<AbstractItem> existing = database.getItemsFors(decoderId, tableType)
        if (existing == null | existing.size() == 0) {
            log.debug("no existing entries for decoder ${decoderId} of type ${tableType}")
            return null
        }
        Hashtable<String, AbstractItem> existingHash = new Hashtable<>()
        existing.each {
            existingHash.put(it.getKey(), it)
        }

        return existingHash
    }

    /**
     * Called to process with the existing data and remove any left over items
     * @param existingHash  HashTable with the remaining items (if any)
     * @param decoderId     the decoder that we are processing
     * @param tableType     which type of diff are working with
     * @param version       the version record for this diff
     * @param
     */
    void diffCleanUp(Hashtable<String, AbstractItem> existingHash,
                     int decoderId,
                     WhichTable tableType,
                     VersionBase version,
                    Closure<AbstractDiff> createClosure ) {
        if (!existingHash) {
            log.debug("no hashtable - nothing to clean up")
            return
        }
        log.debug("Cleaning up the hash with a size of ${existingHash.size()}")
        if (existingHash.size() == 0) {
            log.debug("no previous entries - done with cleanup")
            return
        }
        ArrayList<String> deleteList = new ArrayList<>()
        if (!version.hasBeenWritten) {
            log.debug("version hasn't been written yet - write it now")
            database.insertVersion(version)
            version.hasBeenWritten = true
        }
        existingHash.eachWithIndex { existingEntry, int i ->
            /*
          create a new diff entry for each entry in the array
          set the values, write it
          then add to the list of entries to be deleted
            */
            AbstractItem entry = existingEntry.value
            log.debug("asking to create a new diff - table type is ${tableType}")
            AbstractDiff newDiff = createClosure(entry)
            log.debug("created a new diff - ${newDiff}")
            newDiff.decoderId = decoderId
            newDiff.versionNumber = version.versionNumber
            newDiff.key = entry.key
            newDiff.oldValue = entry.value
            deleteList.add(newDiff.key)
            database.insertDiff(newDiff, tableType)
        }
        database.deleteOldItems(tableType, decoderId, deleteList)
    }

    void importFunctionLabels(def entryList, int decoderId, DecoderEntry decoderEntry) {
        log.debug("Processing the labels for id ${decoderId} and entry ${decoderEntry}")
        int functionLabelSize = entryList.'functionlabels'.functionlabel.size()
        log.debug("functionLabelSize is ${functionLabelSize}")
        VersionBase functionVersion = diffPhaseOne(decoderId, WhichTable.LABEL)
        boolean newDecoderFunctions = false
        Hashtable<String, AbstractItem> functionHash = diffPhaseTwo(decoderId, WhichTable.LABEL)
        if (functionHash == null) {
            newDecoderFunctions = true
        }
        for (labelEntry in 0..<functionLabelSize) {
            log.debug("LabelEntry (index) is ${labelEntry}")
            log.debug("this function label entry has ${entryList.'functionlabels'.functionlabel[labelEntry].'@num'.text()} and ${entryList.'functionlabels'.functionlabel[labelEntry].text()}")
            FunctionLabel funcLab = new FunctionLabel()
            funcLab.decoderId = decoderEntry.id
            funcLab.functionNum = entryList.'functionlabels'.functionlabel[labelEntry].'@num'.text()
            funcLab.functionLabel = entryList.'functionlabels'.functionlabel[labelEntry].text()
            String lockable = entryList.'functionlabels'.functionlabel[labelEntry].'@lockable'.text()
            if ("true".equals(lockable)) {
                funcLab.locked = true
            } else {
                funcLab.locked = false
            }
            log.debug("new function label is ${funcLab}")
            if (newDecoderFunctions) {
                database.insertFunctionLabel(funcLab)
            } else {
                LabelDiff diff = new LabelDiff()
                boolean useUpdate = processDiff(functionHash, funcLab, functionVersion, diff)
                if (diff.wasChanged()) {
                    // item was changed - write it
                    log.debug("found a changed item - ${diff}")
                    database.insertFunctionLabel(funcLab, useUpdate)
                    database.insertLabelDiff(diff)
                }
            }
        }
        diffCleanUp(functionHash, decoderId, WhichTable.LABEL, functionVersion,
                {AbstractItem oldItem ->
                    AbstractDiff newDiff = new LabelDiff()
                    newDiff.oldLocked = ((FunctionLabel)oldItem).locked
                    return newDiff
                })
    }

    /**
     * Takes the values - checks to see if it is already in the hash, and compares
     * @param newItem the new item
     * @param version the version record, which might need to be written
     * @param diff the appropriate diff record
     * @return true if the item was found (and thus needs to be updated)
     *      deletes the existing item from the hash if found
     *      sets the createdOn to dbTime
     *      fills in the diff fields
     *      sets the versionNumber to the value passed in the version item
     *      newItem won't be null (wouldn't be here if that would be the case)
     *      existing might be null if it wasn't found (and thus shouldn't be in the hash)
     */
    boolean processDiff(Hashtable<String, AbstractItem> existingHash, AbstractItem newItem, VersionBase version, AbstractDiff diff) {
        log.debug("process new ${newItem} with version of ${version}")
        boolean updateNeeded
        boolean changed
        AbstractItem existing = existingHash.get(newItem.getKey())
        diff.versionNumber = version.versionNumber
        diff.decoderId = newItem.decoderId
        diff.newValue = newItem.value
        diff.setKey(newItem.getKey())
        if (newItem.decoderId == 146 && newItem instanceof CvValues) {
            if (newItem.getKey().equals("3")) {
                log.debug("got here now")
            }
        }
        if (existing == null) {
            updateNeeded = false
            changed = true
            log.debug("no existing item - an insert is needed ")
        } else {
            updateNeeded = true
            version.createdOn = dbTime
            diff.decoderId = newItem.decoderId
            diff.versionNumber = version.versionNumber
            existingHash.remove(newItem.getKey())
            newItem.setNewValue(diff)
            existing.setOldValue(diff)
            changed = diff.wasChanged()
            log.debug("testing version ")

        }
        if (changed && !version.hasBeenWritten) {
            log.debug("inserting the version record - ${version}")
            database.insertVersion(version)
        }
        return updateNeeded
    }


    RosterEntry importRoster(Component parent, File rosterFile) {
        log.debug("importing from ${rosterFile.path} - getting the lock")
        if (!importLock.tryAcquire()) {
            throw new RuntimeException("attempting to import a file while an import is in progress")
        }
        dbTime = importDb.getCurrentDbTime()
        Timestamp rosterUpdate = new Timestamp(rosterFile.lastModified())
        String rosterText = rosterFile.text
        Log4JStopWatch importTime = new Log4JStopWatch("import", "Starting the import")
        ProgressMonitor monitor = new ProgressMonitor(parent, "Importing Decoders", "Reading XML", 0, 1)
        int arraySize = 0
        RosterEntry thisEntry = null
        try {
            def rosterValues = new XmlSlurper().parseText(rosterText)
            arraySize = rosterValues.roster.locomotive.size()
            def entryList = rosterValues.roster.locomotive
            buildDecoderList()
            thisEntry = getRosterEntry(rosterFile.path)
            boolean rosterFound = false
            HashMap<String, DecoderEntry> existingList = null

            if (thisEntry == null) {
                log.debug("roster not found -- adding new")
                thisEntry = new RosterEntry()
                thisEntry.fullPath = rosterFile.path
                thisEntry.systemName = getSystemName()
                thisEntry.fileDate = rosterUpdate
                thisEntry.entryTime = dbTime
                thisEntry = importDb.addRoster(thisEntry)
                log.debug("this entry is now ${thisEntry}")
            } else {
                rosterFound = true
                thisEntry.fileDate = rosterUpdate
                thisEntry.dateUpdated = dbTime
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
                log.debug("this entry has an id of ${entryList[i].'@id'.text()}")
                Log4JStopWatch individualStopWatch = new Log4JStopWatch("indiv", "each roster entry${entryList[i].'@id'.text()}")
                DecoderEntry newEntry = new DecoderEntry()
                boolean decoderExists = false
                setLocoValues(newEntry, entryList[i], thisEntry)
                database.beginTransaction()
                if (rosterFound) {
                    DecoderEntry previous = existingList.get(entryList[i].'@id'.text())
                    if (previous != null) {
                        if (newEntry.decoderTypeId != previous.decoderTypeId) {
                            existingList.remove(newEntry.decoderId)
                            log.info("Decoder type was changed for entry ${previous.decoderId} with DCC address ${previous.dccAddress}")
                            // since type was changed, we need to delete the old (to delete all dependents) and the reinsert
                            importDb.deleteDecoderEntry(previous)
                            decoderExists = false
                        } else {
                            newEntry = previous
                            decoderExists = true
                            existingList.remove(previous.decoderId)
                        }

                    }
                }
                if (!rosterFound | (rosterFound & !decoderExists)) {
                    log.debug("no database entry found -- inserting")
                    database.addDecoderEntry(newEntry)
                } else {
                    log.debug("existing entry being updated id = ${newEntry.id}")
                    database.updateDecoderEntry(newEntry)
                }
                individualStopWatch.stop()
                // check for additional information in the roster - function labels, attribute pairs and speed profile
                def functions = entryList[i].'functionLabels'
                def functionEntries = functions.'functionlabel'
                if (functionEntries != null) {
                    Log4JStopWatch functionsStopWatch = new Log4JStopWatch("functions", "function entries")
                    importFunctionLabels(entryList[i], newEntry.id, newEntry)
                    functionsStopWatch.stop()
                }
                int keyValuesSize = entryList[i].attributepairs.size()
                def keyPairs = entryList[i].attributepairs
                log.debug("key value size is ${keyValuesSize}")
                importKeyDefs(keyPairs, newEntry.id, newEntry)
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
                log.debug("now commiting this decoder")
                database.commitWork()
            }
            if (rosterFound && existingList.size() > 0) {
                log.debug("still have some old existing decoder entries -- removing them -- ${existingList.size()}")
                existingList.each { entry ->
                    DecoderEntry currentEntry = existingList.get(entry.key)
                    database.beginTransaction()
                    log.debug("this entry is ${currentEntry}")
                    importDb.deleteDecoderEntry(currentEntry)
                    database.commitWork()
                }
            }
            if (rosterFound) {
                thisEntry.dateUpdated = dbTime
                importDb.updateRosterEntry(thisEntry)
            }
            rosterStopWatch.stop()
        }
        catch (Exception e) {
            log.error("Caught an exception working with the import", e)
            database.rollbackAll()
        } finally {
            importLock.release()
            log.trace("closing the progress monitor")
            SwingUtilities.invokeLater {
                monitor.setProgress(arraySize)
                monitor.close()
            }
            importTime.stop()
        }
        log.debug(" there are  ${arraySize} entries in this roster - releasing the lock ")
        thisEntry.decCount = arraySize
        importTime.stop()
        return thisEntry
    }

    HashMap<String, DecoderEntry> updateRosterEntries(RosterEntry thisEntry) {
        log.debug("updating an existing roster")
        ArrayList<Integer> rosterList = new ArrayList()
        rosterList.add(thisEntry.id)
        List<DecoderEntry> existingList = database.decodersForRosterList(rosterList)
        HashMap<String, DecoderEntry> oldLocos = new HashMap<>()
        existingList.each {
            oldLocos.put(it.decoderId, it)
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
        entry.importDate = dbTime
        entry.shunt = thisEntry.'@IsShuntingOn'
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

    static Timestamp doDateModified(String dateValue) {
        if (dateValue == null || dateValue.equals("")) {
            return new Timestamp(new Date().getTime())
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
            Date date = dateFormat.parse(dateValue)
            return new Timestamp(date.getTime())
        } catch (ParseException ex) {
            log.debug("data parse exception -- trying SimpleDateFormat", ex)
            try {

                return new Timestamp(DateFormat.getTimeInstance().parse(dateValue).getTime())
            } catch (ParseException ex2) {
                log.debug("that didn't work -- trying custom format", ex2)
                DateFormat customFmt = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a")
                try {
                    return new Timestamp(customFmt.parse(dateValue).getTime())
                } catch (ParseException ex3) {
// then try with a specific format to handle e.g. "01-Oct-2016 9:13:36"
                    customFmt = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss")
                    return new Timestamp(customFmt.parse(dateValue))
                }
            } catch (IllegalArgumentException ex2) {
                log.error("Illegal argument for DateUpdated -- setting to current date", ex2)
            }
        }
        return new Timestamp(new Date().getTime())
    }

    void importCVs(Object cvXML, int decoderId, DecoderEntry decoder) {
        log.debug("imprting CV values for decoder ${decoderId}")
        VersionBase version = diffPhaseOne(decoderId, WhichTable.CV)
        def cvValues = cvXML.'CVvalue'
        log.trace("CV array size is ${cvValues.size()}")
        boolean newCVs = false
        Hashtable<String, AbstractItem> existingHash = diffPhaseTwo(decoderId, WhichTable.CV)
        if (!existingHash) {
            log.debug("no CV's yet for this decoder")
            newCVs = true
        }
        for (j in 0..<(cvXML.'CVvalue'.size())) {
            String cvName = cvXML.'CVvalue'[j].'@name'
            String cvValue = cvXML.'CVvalue'[j].'@value'
            log.debug("processing cv value ${cvName} with a value of ${cvValue}")
            CvValues cvs = new CvValues()
            cvs.decoderId = decoderId
            cvs.cvNumber = cvName
            cvs.cvValue = cvValue
            log.debug("new CVvalues is ${cvs}")
            if (newCVs) {
                database.insertCVs(cvs, false)
            } else {
                CV_Diff diff = new CV_Diff()
                diff.cvNumber = cvs.cvNumber
                diff.newValue = cvs.cvValue
                boolean useUpdate = processDiff(existingHash, cvs, version, diff)
                if (cvs.cvNumber.equals("3") || cvs.cvNumber.equals("4")) {
                    log.debug("cnumber is 3 or 4 - diff is ${diff}")
                }
                if (diff.wasChanged()) {
                    log.debug("CVs entry was changed ${diff}")
                    database.insertCVs(cvs, useUpdate)
                    database.insertDiff(diff, WhichTable.CV)
                }
            }
        }
        if (existingHash && existingHash.size() > 0) {
            log.debug("Have some CV values to remove ${existingHash.size()}")
            if (!version.hasBeenWritten) {
                database.insertVersion(version)
                version.hasBeenWritten = true
            }
            diffCleanUp(existingHash,
                decoderId,
                WhichTable.CV,
                    version,
                    {   AbstractItem oldItem ->
                        AbstractDiff newDiff = new CV_Diff()
                        log.debug("in the closure - new diff is ${newDiff}")
                        return newDiff
                    })
        }
        if (!version.hasBeenWritten) {
            log.debug("no changes - time update")
        }
        database.updateDetailTime(decoderId, dbTime)
    }

    void importDetail(Component parent, List<Integer> decoders) {
        log.debug("importing details for ${decoders.size()} decoders")
        if (detailLock.tryAcquire()) {
            log.debug("lock acquired XXXXX")
        } else {
            log.error("second import requested")
            throw new RuntimeException("attempt to run a second import")
        }
        dbTime = importDb.getCurrentDbTime()
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
            monitor.setIntermediateOverall(1, 4, "Read Decoder Entry", "Step 1 of 4")
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
            monitor.setIntermediateProgress(2, "Read XML File", "Step 2 of 4")
            String decoderFileName = rosterFiles.get(decoderEntry.rosterId) +
                    File.separator + "roster" + File.separator + decoderEntry.fileName
            boolean fileFound = false
            String decoderText
            try {
                File xmlDecoderFile = new File(decoderFileName)
                decoderText = xmlDecoderFile.text
                fileFound = true
            } catch (FileNotFoundException e) {
                log.error "File ${decoderFileName} was not found"
            }
            if (fileFound) {
                monitor.setIntermediateProgress(3, "Parse XML File", "Step 3 of 4")
                log.debug("found roster xml for id ${decoderEntry.id}")
                XmlSlurper slurper = new XmlSlurper()
                slurper.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
                slurper.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
                try {
                    Log4JStopWatch individualStopWatch = new Log4JStopWatch("details1", "decoder id = ${decoderId}")
                    database.beginTransaction()
                    def decoderXML = slurper.parseText(decoderText)
                    int varSize = decoderXML.'locomotive'.'values'.'decoderDef'.'varValue'.size()
                    // clean out any old CV values and DecoderDef rows first

                  //  database.prepareDetail(decoderEntry.id)
                    int cvSize = decoderXML.'locomotive'.'values'.'CVvalue'.size()
                    monitor.setIntermediateProgress(4, "Add CV records", "Step 4 of 4")
                    log.debug("CV size is ${cvSize}")
                    monitor.setDetailOverall(0, cvSize)
                    importCVs(decoderXML.'locomotive'.'values', decoderId, decoderEntry)
                 /*
                    for (j in 0..<cvSize) {
                        monitor.setDetailProgress(j, "${j} of ${cvSize}")
                        String name = decoderXML.'locomotive'.'values'.'CVvalue'[j].'@name'
                        String cvValue = decoderXML.'locomotive'.'values'.'CVvalue'[j].'@value'
                        log.debug("adding a CV number ${name} with value ${cvValue}")
                        CvValues cVvalues = new CvValues()
                        cVvalues.cvNumber = name
                        cVvalues.cvValue = cvValue
                        cVvalues.decoderId = decoderId
                        database.insertCVs(cVvalues)
                    }
                    database.updateDetailTime(decoderId)
                 */   database.commitWork()
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