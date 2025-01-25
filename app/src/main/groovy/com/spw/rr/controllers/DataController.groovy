package com.spw.rr.controllers


import com.spw.rr.database.DecoderEntry
import com.spw.rr.models.DataModel
import com.spw.rr.viewdb.ViewDbService
import com.spw.rr.views.DataView
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import java.awt.event.ActionEvent

class DataController {

    static final String[] STD_TITLE = ["Decoder Id\n \n ", "DCC Address", "CV 1\nShort Address", "CV 2\nStart Volts", "CV 3\nAcceleration",
                                       "CV 4\nDeceleration", "CV 5\nVHigh", "CV 6\nVmid", "CV 7\nVersion Number", "CV 8\nManufacturer",
                                       "CV 9\nPWM Period", "CV 10\nBEMF Cutout", "CV 11\nPacket Time", "CV 12\nPower Source",
                                       "CV 13\nDC F1-F8", "CV 14\nDC F0,F9-F12", "CV 15\nDecoder Key", "CV 16\nDecoder Lock",
                                       "CV 17\nAddress High", "CV 18\nAddress Low", "CV 19\nConsist"]
    private static final Logger log = LoggerFactory.getLogger(DataController.class)
    static final String[] STD_CVS = ["1", "2", "3", "4", "5", "6", "7", "8", "9",
                                     "10", "11", "12", "13", "14", "15", "16", "17", "18", "19"]
    String cvList
    Vector<Integer> decoderIds
    JDialog parent

    public enum ViewType {
        SELECTED_CVS,       // Decoders down, CVs across
        ALL_CVS,            // Decoders aaross, CVs down
        STANDARD_CVS,       // CVs across, 3 lines, decoders down
        FUNCTION_LABELS,    //  ?
        SPEED_PROFILE,      //  decoders across, profiles down
        KEY_PAIRS,          // decoders down, pairs across
        DECODER_DETAIL      // decoders across, defs down

    }

    ViewType viewType
    ViewDbService database = ViewDbService.getInstance()
    DataModel model
    DataView view

    DataController(JDialog parent, ViewType viewType, List<Integer> decIds) {
        this.parent = parent
        this.viewType = viewType
        this.decoderIds = new Vector<>()
        decIds.each {
            decoderIds.add(it)
        }
        log.debug("added ${this.decoderIds.size()}")
        model = new DataModel(this)
        model.init()
        switch (viewType) {
            case ViewType.SELECTED_CVS: buildSelectedCvs()
                break
            case ViewType.ALL_CVS: buildAllCvs()
                break
            case ViewType.STANDARD_CVS: buildStdCvs()
                break
            case ViewType.FUNCTION_LABELS: buildFunctionLabels()
                break
            case ViewType.DECODER_DETAIL: buildDecPetail()
                break
            default:
                throw new RuntimeException("Unrecognized View type ${viewType}")
        }
    }

    DataController(JDialog parent, ViewType viewType, List<Integer> decoderIds, String cvList) {
        this.cvList = cvList
        DataController(parent, viewType, decoderIds)
    }

    void buildSelectedCvs() {

    }

    void buildAllCvs() {

    }

    void buildStdCvs() {
        view = new DataView(parent, this, model, "Standard CV View", "stdview")
        STD_TITLE.each {
            model.columnNames.add(it)
        }
        log.debug("creating a list of Standard CVs for the decoders: ${decoderIds}")
        ArrayList<ArrayList<String>> lineList = new ArrayList<>()
        List<DecoderEntry> cvelements = database.listStandardCVs(decoderIds)
        log.debug("cvevelements is ${cvelements}")
        Hashtable<String, String> cvHash = new Hashtable<>()
        cvelements.each { entry ->
            cvHash.clear()
            entry.cvValues.each { cvVal ->
                cvHash.put(cvVal.cvNumber, cvVal.cvValue)
            }
            ArrayList thisLine = new ArrayList()
            thisLine.add(entry.roadName + entry.roadNumber)
            STD_CVS.each { nextCV ->
                String thisValue = cvHash.get(nextCV)
                String value = thisValue == null ? " " : thisValue
                thisLine.add(value)
            }
            log.trace("cvHash is ${cvHash}")
            lineList.add(thisLine)
            log.debug("add a line: ${lineList}")
        }
        model.tableList.addAll(lineList)
        log.trace("about to invoke the init for the view")
        SwingUtilities.invokeLater {
            log.debug("invoking the view init")
            view.init()
        }
    }

    void buildFunctionLabels() {

    }

    void buildSpredProfile() {

    }

    void buildKeyPairs() {

    }

    void buildDecPetail() {

    }


    def closeAction = { ActionEvent e ->
        log.debug("Close action has bee requested")
        model.dialog.setVisible(false)
    }

    def helpActiom = { ActionEvent e ->
        log.debug("help action requested")
    }
}
