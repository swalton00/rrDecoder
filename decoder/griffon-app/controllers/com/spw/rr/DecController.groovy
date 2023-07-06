package com.spw.rr

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor

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

    private boolean inited = false

    void onWindowShown(String name, Object window) {
        if (name.equals("decWindow")) {
            log.debug("in the initialization phase - about to get the database data")
            inited = true
            ArrayList<DecoderEntry> dataForTable = database.listDecoders()
            if (dataForTable != null) {
                model.tableList.clear()
                dataForTable.each {
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
            for (i in 0..<model.preferredWidths.size()) {
                view.completeTable.getColumnModel().getColumn(i).setPreferredWidth(model.preferredWidths[i])
            }
        }
    }

}
