package com.spw.rr.controllers

import com.spw.rr.models.MainModel
import com.spw.rr.utilities.BackgroundWorker
import com.spw.rr.utilities.DatabaseServices
import com.spw.rr.utilities.PropertySaver
import com.spw.rr.utilities.Settings
import com.spw.rr.views.MainView
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.SwingUtilities
import java.awt.event.ActionEvent

class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class)

    MainView view
    MainModel model
    Settings settings =  new Settings()
    BackgroundWorker worker = BackgroundWorker.getInstance()
    DatabaseServices databaseServices = DatabaseServices.getInstance()
    PropsController pc

    PropertySaver saver = PropertySaver.getInstance()

    void init() {
        saver.init()
        model = new MainModel(this)
        view = new MainView(this, model)
        settings.loadSettings()
        SwingUtilities.invokeAndWait {
            view.init()
        }
        if (settings.settingsComplete) {
            log.debug("settings are now complete - calling validate to verify")
            settings.settingsValid = databaseServices.validate((settings))
        }
        if (!settings.settingsValid | !settings.settingsComplete) {
            log.debug("don't have a valid settings yet - going directly to prefs")
            createProps()
        }
    }

    def closeAtion = { ActionEvent event ->
        log.info("shutting down the RRdecoder application")
        saver.writeValues()
        System.exit(0)
    }

    def importAction = { ActionEvent event ->
        log.debug("import requested")
    }

    def createProps = { ->
        pc = new PropsController()
        pc.init(model.baseFrame, settings)
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
