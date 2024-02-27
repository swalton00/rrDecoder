package com.spw.rr

import com.spw.rr.mappers.DecoderEntry
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Nonnull
import javax.inject.Inject
import javax.swing.table.DefaultTableCellRenderer

@ArtifactProviderFor(GriffonController)
class CvSpecificController {
    @MVCMember @Nonnull
    CvSpecificModel model

    @MVCMember
    CvSpecificView view

    private static final Logger log = LoggerFactory.getLogger(CvSpecificController.class)

    @Inject
    DecoderDBService database

    @ControllerAction
    void helpAction() {
        log.debug("help has been requested from the CV Specific window")
    }
    /**
     * when the window message is received:
     *    split the string on "," and trim the resulting String array members
     *    call the database with an integer array (the decoder ID's) and a String array (the CV names)
     *    the result will be an array of DecoderEntry's, each with an array of CVvalues
     */
    void onSpecificCV() {
        log.debug("got the specificCv message")
        List<String> cvList = model.cvList.split(",")
        ArrayList<String> newList = new ArrayList<>()
        cvList.each {
            newList.add(it.trim())
        }
        cvList = newList
        log.debug("the cvlist is ${cvList}")
        List<DecoderEntry> decoderList = database.listCVsForDecoder(model.selectedRows, cvList )
        log.debug("decoderList is ${decoderList}")
        model.tableList.clear()
        HashMap<String, String> cvHeader = new HashMap<>()
        int cnt = 1
        ArrayList<String> header = new ArrayList<>()
        header.add("DCC Address")
        newList.each {
            header.add(it)
            cvHeader.put(it, cnt)
            cnt++
        }
        int columnCount = header.size()
        model.columnNames.clear()
        model.columnNames.addAll(header)
        model.tableList.clear()
        decoderList.each {
            ArrayList<String> tempList = new ArrayList<>()
            columnCount.each {
                tempList.add(" ")
            }
            tempList.putAt(0, it.dccAddress)
            it.cvValues.each { cvVal ->
                String item = cvVal.cvNumber
                tempList.putAt(cvHeader.get(item), cvVal.cvValue)
            }
            model.tableList.add(tempList)
        }
        if (view.tableModel != null) {
            runInsideUISync {
                model.tableModel = new RRTableModel((RRBaseModel) model)
                DefaultTableCellRenderer rightRenderer = new RightTableCellRenderer()
                model.theTable.setModel(model.tableModel)
                for (i in 1..<model.columnNames.size()) {
                    model.theTable.getColumnModel().getColumn(i).setCellRenderer(rightRenderer)
                }
                log.debug("changing the data model")
                view.tableModel.dataChanged()
                log.debug("data model now changed")
            }
        } else {
            log.error("view didn't have tableModel")
        }
        application.getWindowManager().show("cvSpecific")
    }
}