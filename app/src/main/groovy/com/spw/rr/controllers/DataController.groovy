package com.spw.rr.controllers

import com.spw.rr.database.CvValues
import com.spw.rr.database.DecoderEntry
import com.spw.rr.models.DataModel
import com.spw.rr.utilities.CvNameComparator
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
        FUNCTION_LABELS,    // decoderrs down, label across
        SPEED_PROFILE,      //  decoders across, profiles down
        KEY_PAIRS,          // decoders down, pairs across
        DECODER_DETAIL      // decoders across, defs down

    }

    ViewType viewType
    ViewDbService database = ViewDbService.getInstance()
    DataModel model
    DataView view

    DataController(JDialog parent, ViewType viewType, List<Integer> decIds) {
        this(parent, viewType, decIds, null)

    }

    DataController(JDialog parent, ViewType viewType, List<Integer> decIds, String cvList) {
        this.cvList = cvList
        this.parent = parent
        this.viewType = viewType
        this.decoderIds = new Vector<>()
        decIds.each {
            this.decoderIds.add(it)
        }
        log.debug("added ${decoderIds.size()}")
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
            case ViewType.SPEED_PROFILE: buildSpeedProfile()
                break
            case ViewType.KEY_PAIRS: buildKeyPairs()
                break
            default:
                throw new RuntimeException("Unrecognized View type ${viewType}")
        }
    }

    void buildAllCvs() {
        view = new DataView(parent, this, model, "All CV View", "allview")
        List<DecoderEntry> decoders = database.listStandardCVs(decoderIds, null, true)
        log.debug("decoder list is ${decoders}")
        model.columnNames.add("CV Number")
        Hashtable<String, String> allCvs = new Hashtable()
        decoders.each {dec ->
            String title = dec.roadName + dec.roadNumber
            if (title.isBlank()) {
                title = dec.dccAddress
            }
            model.columnNames.add(title)
            dec.metaClass.cvHash = new Hashtable<String, String>()
            dec.cvValues.each { CvValues  cvVals->
                dec.cvHash.put(cvVals.cvNumber, cvVals.cvValue)
                if (!allCvs.contains(cvVals.cvNumber)) {
                    allCvs.put(cvVals.cvNumber, cvVals.cvValue)
                }
            }
        }
        log.debug("there are ${allCvs.size()} entries in the overall hashtable")
        ArrayList<String> theKeys = new ArrayList<>(allCvs.keySet())
        log.debug("the keys list is ${theKeys}")
        theKeys.sort(new CvNameComparator())
        ArrayList<ArrayList<String>> allLines  = new ArrayList<>()
        theKeys.each {String cvNum ->
            ArrayList<String> thisLine = new ArrayList<>()
            thisLine.add(cvNum)
            decoders.each {DecoderEntry dec ->
                String val = dec.cvHash.get(cvNum)
                if (val == null) {
                    val = " "
                }
                thisLine.add(val)
            }
            allLines.add(thisLine)
        }

        model.tableList.addAll(allLines)
        SwingUtilities.invokeLater {
            log.debug("build all CVs now invoking view.init")
            view.init()
        }
    }

    void buildSelectedCvs() {
        view = new DataView(parent, this, model, "Selected CV View", "selview")
        List<String> cvSplit = cvList.split(",")
        cvSplit.each {
            it.trim()
        }
        model.columnNames.add("Decoder ID")
        model.columnNames.add("DCC Address")
        cvSplit.each {
            model.columnNames.add(it)
        }
        List<DecoderEntry> cvelements = database.listStandardCVs(decoderIds, cvSplit, false)
        String[] tempString = new StringBuffer[cvSplit.size()]
        cvSplit.eachWithIndex{ String entry, int i ->
            tempString[i] = cvSplit.get(i)
        }
        cvRest(cvelements, tempString)

    }

    void cvRest(List<DecoderEntry> cvelements, String[] cvList) {
        ArrayList<ArrayList<String>> lineList = new ArrayList<>()
        log.debug("cvevelements is ${cvelements}")
        Hashtable<String, String> cvHash = new Hashtable<>()
        cvelements.each { entry ->
            cvHash.clear()
            entry.cvValues.each { cvVal ->
                cvHash.put(cvVal.cvNumber, cvVal.cvValue)
            }
            ArrayList thisLine = new ArrayList()
            thisLine.add(entry.roadName + entry.roadNumber)
            thisLine.add(entry.dccAddress)
            cvList.each { nextCV ->
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

    void buildStdCvs() {
        view = new DataView(parent, this, model, "Standard CV View", "stdview")
        STD_TITLE.each {
            model.columnNames.add(it)
        }
        log.debug("creating a list of Standard CVs for the decoders: ${decoderIds}")
        List<DecoderEntry> cvelements = database.listStandardCVs(decoderIds, null, false)
        cvRest(cvelements, STD_CVS)
    }

    void buildFunctionLabels() {
        view = new DataView(parent, this, model, "Function Label View", "labelview")
        log.debug("creating a list of function labels for decoders")
    }

    void buildSpeedProfile() {

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
