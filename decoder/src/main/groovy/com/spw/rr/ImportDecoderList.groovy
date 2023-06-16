package com.spw.rr


import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ImportDecoderList {
    private static final Logger log = LoggerFactory.getLogger(ImportDecoderList.class)


    void importList(File fileName) {
        log.debug("import from " + fileName.getName())

    }
}
