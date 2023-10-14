package com.spw.rr

import com.spw.rr.mappers.DecoderEntry
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading

import javax.inject.Inject

@ArtifactProviderFor(GriffonController)
class DecController {

    @MVCMember
    DecModel model

    @MVCMember
    DecView view

    @Inject
    DecoderDBService database

    @Inject
    RosterImport importService

    @ControllerAction
    mainAction() {

    }

    @ControllerAction
    helpAction() {

    }

    @ControllerAction
    resetFiltersAction() {
        log.debug("resetting all filters")
        model.filterNoDetails = true
        model.filterDetails = true
        model.filterNoSpeed = true
        model.filterSpeed = true
        model.enableResetFilters = false
        rebuildDisplay()
    }

    private void resetAndShow() {
        model.enableResetFilters = true
        rebuildDisplay()
    }

    private void rebuildDisplay() {
        log.debug("rebuilding display from saved list")
        ArrayList<DecoderEntry> tempList = new ArrayList<>()
        model.completeList.each {
            if (checkFilters(it)) {
                tempList.add(it)
            }
        }
        buildWindowArray(tempList)
    }

    private boolean checkFilters(DecoderEntry entry) {
        log.debug("checking filters for id ${entry.id}")
        // remember -- filter = true means DON'T filter on this
        boolean returnValue = true
        if (!model.filterSpeed) {
            returnValue = returnValue & ! (entry.hasSpeedProfile == null)
        }
        if (!model.filterNoSpeed) {
            returnValue = returnValue & (entry.hasSpeedProfile == null)
        }
        if (!model.filterDetails) {
            returnValue = returnValue & ! (entry.hasDetail == null)
        }
        if (!model.filterNoDetails) {
            returnValue = returnValue & (entry.hasDetail == null)
        }
        log.debug("returning a value of ${returnValue}")
        return returnValue
    }

    @ControllerAction
    speedWithAction() {
        log.debug("filtering on decoders with speed profiles")
        model.filterSpeed = false // turn off enable -- will check for converse in filter
        model.filterNoSpeed = true
        resetAndShow()
    }

    @ControllerAction
    speedWithoutAction() {
        log.debug("filtering on decoders withOUT speed profiles")
        model.filterNoSpeed = false
        model.filterSpeed = true
        resetAndShow()
    }

    @ControllerAction
    detailsWithAction() {
        log.debug("filtering on decoders with details")
        model.filterDetails = false
        model.filterNoDetails = true
        resetAndShow()
    }

    @ControllerAction
    detailsWithoutAction() {
        log.debug("filtering on decoders withOUT details")
        model.filterNoDetails = false
        model.filterDetails = true
        resetAndShow()
    }

    @ControllerAction
    viewSpeedProfilesAction() {

    }

    @ControllerAction
    viewCVvaluesAction() {

    }

    @ControllerAction
    viewDecoderDetailAction() {

    }

    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    void onDecWindow() {
        log.debug("got a DecWindow event -- checking the model for actions")
        switch (model.currentAction) {
            case DecModel.WindowAction.NONE:
                return
            case DecModel.WindowAction.LIST_BY_SELECTION:
                listSelection()
                break
            case DecModel.WindowAction.LIST_ALL:
                listAll()
            case DecModel.WindowAction.LIST_ALL:
                break
            case DecModel.WindowAction.LIST_BY_SELECTION:
                break
            case DecModel.WindowAction.CLOSE:
                break
            case DecModel.WindowAction.SHOW:
                break
            case DecModel.WindowAction.NONE:
                break
        }
    }

    private listSelection() {
        log.debug("listing the selection")
        doListSelection((int[]) model.selectedRows.toArray())
    }

    private doListSelection(int[] theList) {
        resetAllFilters()
        model.completeList = database.listDecodersByRosterID(theList)
        buildWindowArray(model.completeList)
        application.getWindowManager().show("decWindow")
    }

    private resetAllFilters() {
        model.enableResetFilters = false
        model.filterNoSpeed = true
        model.filterSpeed = true
        model.filterNoDetails = true
        model.filterDetails = true
    }

    private listAll() {
        int[] dummyList = new int[0]
        doListSelection(dummyList )
    }

    private void buildWindowArray(ArrayList<DecoderEntry> entries) {
        if (entries != null) {
            model.tableList.clear()
            entries.each {
                ArrayList<String> newArray = new ArrayList<String>()
                newArray.add(it.id.toString())
                newArray.add(it.rosterId.toString())
                newArray.add(it.fileName)
                newArray.add(it.roadName)
                newArray.add(it.roadNumber)
                newArray.add(it.manufacturer)
                newArray.add(it.owner)
                newArray.add(it.dccAddress)
                newArray.add(it.dateUpdated)
                model.tableList.add(newArray)
            }
            if (view.tableModel != null) {
                runInsideUISync {
                    log.debug("changing the data model")
                    view.tableModel.dataChanged()
                    log.debug("data model now changed")
                }
            }
        }
    }

    boolean inited = false

    @ControllerAction
    void importDetailAction() {
        log.debug("import detail action requested")
        importService.importDetailDecoders(model.selectedRows)
    }

    void onWindowShown(String name, Object window) {
        if (name.equals("decWindow")) {
            log.debug("in the initialization phase - about to get the database data")
            inited = true
            for (i in 0..<model.preferredWidths.size()) {
                view.completeTable.getColumnModel().getColumn(i).setPreferredWidth(model.preferredWidths[i])
            }
        }
    }
}
