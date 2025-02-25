package com.spw.rr.controllers

import com.spw.rr.database.AbstractItem
import com.spw.rr.database.DecoderEntry
import com.spw.rr.models.DataModel
import com.spw.rr.utilities.CvNameComparator
import com.spw.rr.utilities.StringCvComparator
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

    String fixRoadName(DecoderEntry entry) {
        String title = entry.roadName + entry.roadNumber
        if (title.isBlank()) {
            title = entry.dccAddress
        }
        return title
    }

    void doColumnHeaders(List<DecoderEntry> entries, ViewType listType) {
        entries.each {
            String title = fixRoadName(it)
            model.columnNames.add(title)
        }
        restDecoderDown(entries, listType)
    }

    void restDecoderDown(List<DecoderEntry> entries, ViewType listType) {
        Hashtable<String, String> hashList = new Hashtable()
        entries.each { dec ->
            dec.keyHash = new Hashtable<String, AbstractItem>()

            List<AbstractItem> arrayObject = dec.getList(listType)
            arrayObject.each {
                dec.keyHash.put(it.getKey(), it)
                hashList.put(it.getKey(), it)
            }
        }
        log.debug("there are ${hashList.size()} entries in the overall hashtable")
        ArrayList<String> theKeys = new ArrayList<>(hashList.keySet())
        log.debug("the keys list is ${theKeys}")

        switch (listType) {
            case ViewType.ALL_CVS: theKeys.sort(new CvNameComparator())
                break
            case ViewType.DECODER_DETAIL: theKeys.sort(new StringCvComparator())
                break
            case ViewType.FUNCTION_LABELS:
            case ViewType.SPEED_PROFILE:
                theKeys.sort((s1, s2) -> Integer.compare(Integer.valueOf(s1), Integer.valueOf(s2)))
                break
            default:
                theKeys.sort((s1, s2) -> s1.compareTo(s2))
        }
        ArrayList<ArrayList<String>> allLines = new ArrayList<>()
        theKeys.each { String keyVal ->
            ArrayList<String> thisLine = new ArrayList<>()
            thisLine.add(keyVal)
            entries.each { DecoderEntry dec ->
                AbstractItem item = dec.keyHash.get(keyVal)
                if (item != null) {
                    item.addRows(thisLine)
                } else {
                    thisLine.add(" ")
                    if (viewType == ViewType.SPEED_PROFILE) {
                        thisLine.add(" ")
                    }
                }
            }
            allLines.add(thisLine)
        }
        model.tableList.addAll(allLines)
        SwingUtilities.invokeLater {
            log.debug("build all CVs now invoking view.init")
            view.init()
        }
    }

    void buildAllCvs() {
        view = new DataView(parent, this, model, "All CV View", "allview")
        List<DecoderEntry> decoders = database.getList(ViewDbService.ListType.ALL_CV, decoderIds, null)
        log.debug("decoder list is ${decoders.size()}")
        model.columnNames.add("CV Number")
        doColumnHeaders(decoders, ViewType.ALL_CVS)
    }

    void buildSelectedCvs() {
        view = new DataView(parent, this, model, "Selected CV View", "selview")
        List<String> cvSplit = cvList.split(",")
        ArrayList<String> newCvList = new ArrayList<>()
        cvSplit.each {
            log.trace("before trim - ${it} - ${it.size()}")
            String newVal = it.strip()
            newCvList.add(newVal)
            log.trace("after trim - ${newVal} - ${newVal.size()}")
        }
        model.columnNames.add("Decoder ID")
        model.columnNames.add("DCC Address")
        newCvList.each {
            model.columnNames.add(it)
            log.trace("size of ${it} - ${it.size()}")
        }
        List<DecoderEntry> cvelements = database.getList(ViewDbService.ListType.CV_LIST, decoderIds, newCvList)
        String[] tempString = new StringBuffer[cvSplit.size()]
        newCvList.eachWithIndex { String entry, int i ->
            tempString[i] = newCvList.get(i)
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
        List<DecoderEntry> cvelements = database.getList(ViewDbService.ListType.FIXED_CVS, decoderIds, null)
        cvRest(cvelements, STD_CVS)
    }

    void buildFunctionLabels() {
        view = new DataView(parent, this, model, "Function Label View", "labelview")
        log.debug("creating a list of function labels for decoders")
        List<DecoderEntry> decs = database.getList(ViewDbService.ListType.LABEL_LIST, decoderIds, null)
        model.columnNames.add("Function Number")
        doColumnHeaders(decs, ViewType.FUNCTION_LABELS)
    }

    void buildSpeedProfile() {
        view = new DataView(parent, this, model, "Speed Profiles", "speedview")
        log.debug("creatinga list of speed profile values for decoders")
        List<DecoderEntry> decs = database.getList(ViewDbService.ListType.SPEED_LIST, decoderIds, null)
        model.columnNames.add("Speed Entry")
        decs.each {
            String hdr = fixRoadName(it)
            model.columnNames.add(hdr + "\n" + "Forward")
            model.columnNames.add(hdr + "\n" + "Reverse")
        }
        restDecoderDown(decs, ViewType.SPEED_PROFILE)
    }

    void buildKeyPairs() {
        view = new DataView(parent, this, model, "Key Value Pairs View", "keyview")
        log.debug("creating a list of key value pairs for decoders")
        List<DecoderEntry> decs = database.getList(ViewDbService.ListType.KEY_VAL_LIST, decoderIds, null)
        model.columnNames.add("Key")
        doColumnHeaders(decs, ViewType.KEY_PAIRS)
    }

    void buildDecPetail() {
        view = new DataView(parent, this, model, "Decoder Definition View", "decdetview")
        log.debug("creating a list of decoder definitions for decoders")
        List<DecoderEntry> decs = database.getList(ViewDbService.ListType.DEF_LIST, decoderIds, null)
        model.columnNames.add("Decoder Def")
        doColumnHeaders(decs, ViewType.DECODER_DETAIL)
    }

    def printSAction = { ActionEvent e ->
        log.debug("Print requested")
        model.theTable.print()
    }

    def closeAction = { ActionEvent e ->
        log.debug("Close action has bee requested")
        model.dialog.setVisible(false)
    }

    def helpActiom = { ActionEvent e ->
        log.debug("help action requested")
    }
}
