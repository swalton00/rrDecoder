package com.spw.rr

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.core.mvc.MVCGroup
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javax.annotation.Nonnull
import javax.inject.Inject
import javax.swing.JFileChooser

@ArtifactProviderFor(GriffonController)
class DecoderController {
    @MVCMember
    @Nonnull
    DecoderModel model
    private static importer = new ImportDecoderList()

    @MVCMember
    DecoderView view

    private RosterImport importService = new RosterImport()

    @Inject
    private DecoderDBService database

    private boolean inited = false

    MVCGroup prefsGroup = null

    @ControllerAction
    void importAction() {
        log.info("Importing a JMRI collection - choosing file now")
        JFileChooser chooser = new JFileChooser()
        chooser.setDialogTitle("Select JMRI decoder index")
        int retVal = chooser.showOpenDialog(null)
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile()
            importService.setDB(database)
            RosterEntry entry = importService.importRoster(selected)
            ArrayList<String> newEntry = new ArrayList<String>()
            newEntry.add(entry.id.toString())
            newEntry.add(entry.systemName)
            newEntry.add(entry.fullPath)
            model.tableList.add(newEntry)
            view.tableModel.dataChanged()
        }

    }

    void onWindowShown(String name, Object window) {
        if (name.equals("mainWindow") & !inited) {
            log.debug("in the initialization phase - about to get the database data")
            inited = true
            ArrayList<RosterEntry> dataForTable = database.listRosters()
            if (dataForTable != null) {
                dataForTable.each {
                    ArrayList<String> newArray = new ArrayList<String>()
                    newArray.add(it.id.toString())
                    newArray.add(it.systemName)
                    newArray.add(it.fullPath)
                    model.tableList.add(newArray)
                }
                if (view.tableModel != null) {
                    view.tableModel.dataChanged()
                }
            }
        }
    }


    private checkGroup(String groupName, MVCGroup group) {
        if (group == null) {

        }
    }

    @ControllerAction
    void exitAction() {
        log.debug("Shutting down now")
        application.shutdown()
    }

    @ControllerAction
    void prefsAction() {
        prefsGroup = checkGroup("prefs", prefsGroup)
        log.debug("showing the Preferences window")
        application.getWindowManager().show("prefs")
    }


    @ControllerAction
    void helpAction() {

    }

    @ControllerAction
    void aboutAction() {

    }
}