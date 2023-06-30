package com.spw.rr

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RosterImport {
    static final Logger log = LoggerFactory.getLogger(RosterImport.class)

    private DecoderDBService database

    void setDB(DecoderDBService database) {
        log.debug("setting the database service address")
        this.database = database
    }

    void importRoster(File rosterFile) {
        log.debug("importing from ${rosterFile.path}" )
        String rosterText = rosterFile.text
        def rosterValues = new XmlSlurper().parseText(rosterText)
        def first = rosterValues.'roster-config'
        int counter = 0
        def firstEntry = rosterValues.roster.locomotive[0]
        int arraySize = rosterValues.roster.locomotive.size()
        def entryList = rosterValues.roster.locomotive
        RosterEntry thisRoster = getRosterEntry(rosterFile.path)
        for (i in 0..<arraySize) {
            log.debug("this entry has an id of ${entryList[i].'@id'.text()}")
        }
        def firstId = firstEntry.'@id'.text()
        log.debug("there are ${arraySize} entries in this roster")
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
        String systemName = System.getenv("COMPUTERNAME")
        return database.getRosterEntry(systemName, fullPath)
    }
}
