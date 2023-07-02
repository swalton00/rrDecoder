package com.spw.rr

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RosterImport {
    static final Logger log = LoggerFactory.getLogger(RosterImport.class)

    private DecoderDBService database

    List<DecoderEntry> decoderList = null

    void setDB(DecoderDBService database) {
        log.debug("setting the database service address")
        this.database = database
    }

    private String getSystemName() {
        log.debug("getting System name")
        return System.getenv("COMPUTERNAME")
    }


    void buildDecoderList() {
        log.debug("now building a list of current decoders")
        if (decoderList == null) {
            decoderList = new ArrayList<DecoderEntry>()
        } else {
            decoderList.clear()
        }
        def newList = database.listDecoders()
        newList.each { entry ->
            decoderList.add(entry)
        }
    }

    DecoderEntry findDecoder(String family, String model) {
        log.debug("finding decoder with family: ${family} and model: ${model}")
        DecoderEntry found = null
        decoderList.each {
            if (it.decoderFamily.equals(family) & it.decoderModel.equals(model)) {
                log.debug("found the decoder")
                found = it
                return
            }
        }
        if (found != null) {
            return found
        }
        found = new DecoderEntry()
        found.decoderModel = model
        found.decoderFamily = family
        found = database.insertDecoderEntry(found)
        decoderList.add(found)
        return found
    }

    void importRoster(File rosterFile) {
        log.debug("importing from ${rosterFile.path}")
        String rosterText = rosterFile.text
        def rosterValues = new XmlSlurper().parseText(rosterText)
        def first = rosterValues.'roster-config'
        int counter = 0
        def firstEntry = rosterValues.roster.locomotive[0]
        int arraySize = rosterValues.roster.locomotive.size()
        def entryList = rosterValues.roster.locomotive
        buildDecoderList()
        RosterEntry thisRoster = getRosterEntry(rosterFile.path)
        boolean rosterFound = false
        if (thisRoster == null) {
            log.debug("roster not found -- adding new")
            thisRoster = new RosterEntry()
            thisRoster.fullPath = rosterFile.path
            thisRoster.systemName = getSystemName()
            thisRoster = database.addRoster(thisRoster)
        } else {
            rosterFound = true
            updateRosterEntries(thisRoster)
        }
        for (i in 0..<arraySize) {
            log.debug("this entry has an id of ${entryList[i].'@id'.text()}")
            LocomotiveEntry newEntry = setLocoValues(entryList[i], thisRoster)
            if (!rosterFound) {
                addLoco(newEntry)
            } else {
                log.error("should have added locomotive")
            }
        }
        def firstId = firstEntry.'@id'.text()
        log.debug("there are ${arraySize} entries in this roster")
    }

    LocomotiveEntry addLoco(LocomotiveEntry entry) {
        database.addLocomotiveEntry(entry)
    }

    LocomotiveEntry setLocoValues(Object thisEntry, RosterEntry rosterEntry) {
        LocomotiveEntry entry = new LocomotiveEntry()
        entry.rosterId = rosterEntry.id
        String decoderModel = thisEntry.decoder.'@model'
        String decoderFamily = thisEntry.'@family'
        log.debug("find decoder for ${decoderModel} with family ${decoderFamily}")
        DecoderEntry decoder = findDecoder(decoderFamily, decoderModel)
        entry.decoderId = decoder.id
        entry.locoId = thisEntry.'@id'
        entry.fileName = thisEntry.'@fileName'
        entry.roadName = thisEntry.'@roadName'
        entry.manufacturer = thisEntry.'@mfg'
        entry.owner = thisEntry.'@owner'
        entry.model = thisEntry.'@model'
        entry.dccAddress = thisEntry.'@dccAddress'
        entry.manufacturerId = thisEntry.'@manufacturerID'
        entry.productId = thisEntry.'@productID'
        return entry
    }

    void updateRosterEntries(RosterEntry thisEntry) {
        log.debug("updating an existing roster")

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
