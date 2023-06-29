package com.spw.rr

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RosterImport {
    static final Logger log = LoggerFactory.getLogger(RosterImport.class)

    void importRoster(File rosterFile) {
        log.debug("importing from ${rosterFile.path}" )
        String rosterText = rosterFile.text
        def rosterValues = new XmlSlurper().parseText(rosterText)
        def first = rosterValues.'roster-config'
        int counter = 0
        def firstEntry = rosterValues.roster.locomotive[0]
        int arraySize = rosterValues.roster.locomotive.size()
        def entryList = rosterValues.roster.locomotive
        for (i in 0..<arraySize) {
            log.debug("this entry has an id of ${entryList[i].'@id'.text()}")
        }
        def firstId = firstEntry.'@id'.text()
        log.debug("there are ${arraySize} entries in this roster")
    }
}
