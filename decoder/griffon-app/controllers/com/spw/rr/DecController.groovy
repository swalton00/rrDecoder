package com.spw.rr

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import static com.spw.rr.DecModel.WindowAction.*
import javax.inject.Inject

@ArtifactProviderFor(GriffonController)
class DecController {

    @MVCMember
    DecModel model

    @MVCMember
    DecView view

    @Inject
    DecoderDBService database

    @ControllerAction
    mainAction() {

    }

    @ControllerAction
    helpAction() {

    }

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
        ArrayList<DecoderEntry> entries = database.listDecodersByRosterID((int[]) model.selectedRows.toArray())
        buildWindowArray(entries)
        application.getWindowManager().show("decWindow")
    }

    private listAll() {
        ArrayList<DecoderEntry> dataForTable = database.listDecoders()
        buildWindowArray(dataForTable)
        application.getWindowManager().show("decWindow")
    }

    private void buildWindowArray(ArrayList<DecoderEntry> entries) {
        if (entries != null) {
            model.tableList.clear()
            entries.each {
                ArrayList<String> newArray = new ArrayList<String>()
                newArray.add(it.rosterId.toString())
                newArray.add(it.id.toString())
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
                view.tableModel.dataChanged()
            }
        }
    }

    boolean inited = false

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
