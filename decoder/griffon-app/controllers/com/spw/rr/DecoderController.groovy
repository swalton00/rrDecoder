package com.spw.rr

import com.spw.rr.mappers.RosterEntry
import griffon.core.GriffonApplication
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.core.mvc.MVCGroup
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading

import javax.annotation.Nonnull
import javax.inject.Inject
import javax.swing.JFileChooser
import org.perf4j.log4j.Log4JStopWatch

import static com.spw.rr.DecModel.WindowAction.LIST_ALL
import static com.spw.rr.DecModel.WindowAction.LIST_BY_SELECTION

@ArtifactProviderFor(GriffonController)
class DecoderController {
    @MVCMember
    @Nonnull
    DecoderModel model
    private static importer = new ImportDecoderList()

    @MVCMember
    DecoderView view

    @Inject
    private RosterImport importService

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
                importService.setRequiredData(database, application)
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

    @ControllerAction
    void importDetailAction() {
        log.debug("begining detailed import action")
        importService.importDetailRoster(model.selectedRows)
    }

    void onWindowShown(String name, Object window) {
        if (name.equals("mainWindow") & !inited) {
            log.debug("in the initialization phase - about to get the database data")
            inited = true
            Log4JStopWatch stopWatch = new Log4JStopWatch("dbLoad", "loading the roster list")
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
                model.theTable.getColumnModel().getColumn(i).setPreferredWidth(model.preferredWidths[i])
            }
            stopWatch.stop()
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
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    void decWindowAction() {
        log.debug("Showing the Decoder list window")
        decWindowGroup = checkGroup("dec", decWindowGroup)
        DecModel decModel = decWindowGroup.getModel()
        decModel.currentAction = LIST_ALL
        application.eventRouter.publishEvent("DecWindow", [])
    }

    @ControllerAction
    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    void decSelectedAction() {
        log.debug("showing a view of the selected rosters")
        decWindowGroup = checkGroup("dec", decWindowGroup)
        DecModel decModel = decWindowGroup.getModel()
        decModel.currentAction = LIST_BY_SELECTION
        decModel.selectedRows = model.selectedRows
        log.debug("selected index is ${model.selectedRows}")
        log.debug("publishing the event")
        application.eventRouter.publishEvent("DecWindow", [])
    }


    @ControllerAction
    void helpAction() {

    }

    @ControllerAction
    void aboutAction() {

    }
}