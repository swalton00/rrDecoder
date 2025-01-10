package com.spw.rr.controllers

import com.spw.rr.models.MainModel
import com.spw.rr.utilities.PropertySaver
import com.spw.rr.views.MainView
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.event.ActionEvent

class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class)

    MainView view
    MainModel model
    PropertySaver saver = PropertySaver.getInstance()

    void init() {
        saver.init()
        model = new MainModel(this)
        view = new MainView(this, model)
        view.init()
    }

    def closeAtion = { ActionEvent event ->
        log.info("shutting down the RRdecoder application")
        saver.writeValues()
        System.exit(0)
    }

    def importAction = { ActionEvent event ->
        log.debug("import requested")
    }

    def settingsAction = { ActionEvent event ->
        log.debug("settings edit requested")
    }

    def viewAction = { ActionEvent event ->
        log.debug("view action requested")
    }

    def helpAction= { ActionEvent event ->
        log.debug("help action requested")
    }

    def aboutAction= { ActionEvent event ->
        log.debug("about action requested")
    }

}
