package com.spw.rr

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading

import javax.inject.Inject

@ArtifactProviderFor(GriffonController)
class CvController {

    @MVCMember
    CvModel model

    @MVCMember
            view

    @Inject
    DecoderDBService database

    @ControllerAction
    void closeAction() {
        log.debug("close has been requested")

    }

    @ControllerAction
    void helpAction() {
        log.debug("help has been requested")
    }
    /**
     *
     */
    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    void onCvWindow() {
        log.debug("got a CV Window event - showing the list from the model")
        /**
         * 1. build list of relevant db rows
         * 2. add them to the table
         * 3. show the window
         * 4. refresh the data in the view
         */
        int[] decList = model.selectedRows.toArray()
        ArrayList<CvShow> showList = database.listStandardCVsFor(decList, model.rosterIds)
        model.tableList.clear()
        log.debug("showList is ${showList}")
        if (showList != null) {
            showList.each {
                ArrayList<String> newArray = new ArrayList<>()
                newArray.add(it.decoderId.toString())
                newArray.add(it.dccAddress)
                newArray.add(it.cv1)
                newArray.add(it.cv2)
                newArray.add(it.cv3)
                newArray.add(it.cv4)
                newArray.add(it.cv5)
                newArray.add(it.cv6)
                newArray.add(it.cv7)
                newArray.add(it.cv8)
                newArray.add(it.cv9)
                newArray.add(it.cv10)
                newArray.add(it.cv11)
                newArray.add(it.cv12)
                newArray.add(it.cv13)
                newArray.add(it.cv14)
                newArray.add(it.cv15)
                newArray.add(it.cv16)
                newArray.add(it.cv17)
                newArray.add(it.cv18)
                newArray.add(it.cv19)
                model.tableList.add(newArray)
                log.debug("adding row with dcc address of ${it.dccAddress}")
            }
        }
        application.getWindowManager().show("cvWindow")
        runInsideUISync {
            log.debug("changing the data model")
            view.tableModel.dataChanged()
            log.debug("data model now changed")
        }
    }

    boolean inited = false


    void onWindowShow(String name, Object window) {
        if (name.equals("dvWindow")) {
            log.debug("in the initialization phase - setting up the window")
            inited = true
            for (i in 0..<model.preferredWidths.size()) {
                view.completeTable.getColumnModel().getColumn(i).setPreferredWidth(model.preferredWidths[i])
            }
        }
    }


}
