package com.spw.rr.controllers

import com.spw.rr.database.RosterEntry
import com.spw.rr.models.MainModel
import com.spw.rr.utilities.BackgroundWorker
import com.spw.rr.utilities.DatabaseServices
import com.spw.rr.utilities.ImportService
import com.spw.rr.utilities.PropertySaver
import com.spw.rr.utilities.Settings
import com.spw.rr.views.MainView
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JFileChooser
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
    ImportService imports = ImportService.getInstance()

    PropertySaver saver = PropertySaver.getInstance()

    void init() {
        saver.init()
        model = new MainModel(this)
        model.init()
        view = new MainView(this, model)
        settings.loadSettings()
        SwingUtilities.invokeAndWait {
            view.init()
        }
        if (settings.settingsComplete) {
            log.debug("settings are now complete - calling validate to verify")
            settings.settingsValid = databaseServices.validate((settings))
            if (settings.settingsValid) {
                databaseServices.dbStart(settings)
            }
        }
        if (!settings.settingsValid | !settings.settingsComplete) {
            log.debug("don't have a valid settings yet - going directly to prefs")
            createProps()
        }
        if (settings.databaseOpen) {
            log.debug("loading the data for the main view")
            List<RosterEntry> entries = databaseServices.listRosters()
            entries.each {
                addEntry(it)
            }
        }
    }

    void addEntry(RosterEntry newEntry) {
        ArrayList<String> nextLine = new ArrayList()
        nextLine.add(newEntry.id.toString())
        nextLine.add(newEntry.systemName)
        nextLine.add(newEntry.decCount)
        nextLine.add(newEntry.fullPath)
        model.tableList.add(nextLine)
        view.tableModel.fireTableDataChanged()
    }

    def closeAction = { ActionEvent event ->
        log.info("shutting down the RRdecoder application")
        saver.writeValues()
        System.exit(0)
    }

    Runnable importBackgrounnd = {  ->
        log.debug("importing roster at location ${chosen}")
        boolean exists = imports.doesRosterExist(chosen)
        RosterEntry newEntry = imports.importRoster(model.baseFrame, chosen)
        SwingUtilities.invokeLater { ->
            addEntry(newEntry)
        }
    }



    File chosen
    def importAction = { ActionEvent event ->
        log.debug("import requested")
        JFileChooser chooser = new JFileChooser()
        chooser.setDialogTitle("Select appropriate roster.xml")
        int retVal = chooser.showOpenDialog(null)
        if (retVal == JFileChooser.APPROVE_OPTION) {
            chosen = chooser.getSelectedFile()
            worker.execute(importBackgrounnd)
        }
    }

    def createProps = { ->
        pc = new PropsController()
        pc.init(model.baseFrame, settings)
    }

    def settingsAction = { ActionEvent event ->
        log.debug("settings edit requested")
        worker.execute(backgroundSettings)
    }

    Runnable backgroundSettings = { ->
        createProps()
    }

    def importDetailAction = { ActionEvent e ->
        log.debug("Import detail has been requested")
        if (model.selectedRows.size() == 0) {
            log.debug("import detail requested, but no rows selected")
            return
        }
        rosterIds.clear()
        model.selectedRows.each {
            rosterIds.add(it)
        }
        worker.execute { ->
            imports.importDetailRoster(model.baseFrame, rosterIds)
        }
    }

    ArrayList<Integer> rosterIds = new ArrayList<>()
    void backgroundView() {
        DecController decController = new DecController(model.baseFrame, rosterIds)
        decController.init()
    }

    def viewAction = { ActionEvent event ->
        log.debug("view action requested from main")
        if (model.selectedRows.size() == 0) {
            log.debug("view action but selected rows is empty")
            return
        }
        rosterIds.clear()
        model.selectedRows.each {
            rosterIds.add(it)
        }
        log.trace("list of roster ids is ${rosterIds}")
        worker.execute(backgroundView())
    }

    def viewAllAction = { ActionEvent event ->
        log.debug("view all requested")
        rosterIds.clear()
        model.tableList.each {
            Integer thisId = Integer.valueOf(it.get(0))
            rosterIds.add(thisId)
        }
        log.trace("Roster ids are ${rosterIds}")
        worker.execute(backgroundView())
    }

    def helpAction= { ActionEvent event ->
        log.debug("help action requested")
    }

    def aboutAction= { ActionEvent event ->
        log.debug("about action requested")
    }

}
