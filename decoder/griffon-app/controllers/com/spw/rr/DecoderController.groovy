package com.spw.rr

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.core.mvc.MVCGroup
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading

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

    MVCGroup decWindowGroup = null

    private boolean inited = false

    MVCGroup prefsGroup = null

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void importAction() {
        log.info("Importing a JMRI collection - choosing file now")
        JFileChooser chooser = new JFileChooser()
        chooser.setDialogTitle("Select JMRI decoder index")
        int retVal = chooser.showOpenDialog(null)
        if (retVal == JFileChooser.APPROVE_OPTION) {
            boolean existingItem = false
            RosterEntry entry = null
            runOutsideUI {
                File selected = chooser.getSelectedFile()
                importService.setDB(database)
                existingItem = importService.doesRosterExist(selected)
                entry = importService.importRoster(selected)
                ArrayList<String> newEntry = new ArrayList<String>()
                if (!existingItem) {
                    newEntry.add(entry.id.toString())
                    newEntry.add(entry.systemName)
                    newEntry.add(entry.fullPath)
                    model.tableList.add(newEntry)
                }
                runInsideUISync {
                    view.tableModel.dataChanged()
                }
            }
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
            for (i in 0..<model.preferredWidths.size()) {
                view.completeTable.getColumnModel().getColumn(i).setPreferredWidth(model.preferredWidths[i])
            }
        }
    }


    private MVCGroup checkGroup(String groupName, MVCGroup group) {
        if (group == null) {
            return application.mvcGroupManager.findGroup(groupName)
        }
        return group
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
    void decWindowAction() {
        log.debug("Showing the Decoder list window")
        decWindowGroup = checkGroup("dec", decWindowGroup)
        application.getWindowManager().show("decWindow")
    }


    @ControllerAction
    void helpAction() {

    }

    @ControllerAction
    void aboutAction() {

    }
}