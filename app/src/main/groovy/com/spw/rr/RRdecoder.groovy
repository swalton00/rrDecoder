package com.spw.rr

import com.spw.rr.controllers.MainController
import org.slf4j.LoggerFactory
import org.slf4j.Logger



class RRdecoder {

    private static final Logger log = LoggerFactory.getLogger(RRdecoder.class)

    public static void main(String[] args) {
        log.info("RRdecoder started")
        MainController app = new MainController()
        app.init()
    }
}
