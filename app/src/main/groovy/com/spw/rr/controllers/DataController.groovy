package com.spw.rr.controllers

import com.spw.rr.database.AbstractDiff
import com.spw.rr.database.AbstractItem
import com.spw.rr.database.CV_Diff
import com.spw.rr.database.CvValues
import com.spw.rr.database.DecoderEntry
import com.spw.rr.database.LabelDiff
import com.spw.rr.database.VersionBase
import com.spw.rr.models.DataModel
import com.spw.rr.utilities.BuildKeyList
import com.spw.rr.utilities.CvNameComparator
import com.spw.rr.utilities.StringCvComparator
import com.spw.rr.utilities.StringIntegerComparator
import com.spw.rr.viewdb.ViewDb
import com.spw.rr.viewdb.ViewDb.DiffType
import com.spw.rr.viewdb.ViewDb.SelectType
import com.spw.rr.viewdb.ViewDbService
import com.spw.rr.views.DataView
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import javax.swing.text.View
import java.awt.event.ActionEvent
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    String printTitle

    public enum ViewType {
        SELECTED_CVS,       // Decoders down, CVs across
        ALL_CVS,            // Decoders across, CVs down
        STANDARD_CVS,       // CVs across, 3 lines, decoders down
        FUNCTION_LABELS,    // decoders down, label across
        SPEED_PROFILE,      // decoders across, profiles down
        KEY_PAIRS,          // decoders down, pairs across
        CV_DIFF_ALL,        // decoders across, with CV versions, CV's down
        CV_DIFF_CHANGED,    //    as above, but only changed cv's
        KEY_DIFF_ALL,       // decoders across, with key versions, key's down
        KEY_DIFF_CHANGED,   //    as above, but only changed keys
        LABELS_DIFF_ALL,    // decoders across, with label changes, labels down
        LABELS_DIFF_CHANGED //    as above, but only changed labels
    }

    ViewType viewType
    ViewDbService database = ViewDbService.getInstance()
    DataModel model
    DataView view


    /**
     * Constructor for use when not selecting specific CVs
     * @param parent the Window parent
     * @param viewType the type of view
     * @param decIds the decoders to show
     */
    DataController(JDialog parent, ViewType viewType, List<Integer> decIds) {
        this(parent, viewType, decIds, null)

    }

    /**
     * Constructor to set required values
     * @param parent the parent Window
     * @param viewType type of view (enum)
     * @param decIds decoders to show (ids for each in an ArrayList)
     * @param cvList the specific CV values requested if ViewType is SelectedCVs (otherwise null)
     */
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
                printTitle = "Selected CV Contents"
                break
            case ViewType.ALL_CVS: buildAllCvs()
                printTitle = "Contents of ALL CV's"
                break
            case ViewType.STANDARD_CVS: buildStdCvs()
                printTitle = "Standard CV Contents"
                break
            case ViewType.FUNCTION_LABELS: buildFunctionLabels()
                printTitle = "Function Labels"
                break
            case ViewType.SPEED_PROFILE: buildSpeedProfile()
                printTitle = "Speed Profiles"
                break
            case ViewType.KEY_PAIRS: buildKeyPairs()
                printTitle = "Key Value pairs"
                break
            case ViewType.CV_DIFF_ALL: buildCvDiffAll()
                printTitle = "CV Changes - All CV's"
                break
            case ViewType.CV_DIFF_CHANGED: buildCvDiff()
                printTitle = "CV Changes - Only Changed CV's"
                break
            case ViewType.KEY_DIFF_ALL: buildKeyDiffAll()
                printTitle = "Changes to Key Value Pairs - all Keys"
                break
            case ViewType.KEY_DIFF_CHANGED: buildKeyDiff()
                printTitle = "Changes to Key Value Pairs - Only Changed Keys"
                break
            case ViewType.LABELS_DIFF_ALL: buildLabelDiffAll()
                printTitle = "Changes to Function Labels - All Functions"
                break
            case ViewType.LABELS_DIFF_CHANGED: buildLabelDiff()
                break
            default:
                throw new RuntimeException("Unrecognized View type ${viewType}")
        }
    }

    /*
        General flow - begin with Constructor
            DataModel is created in constructor
            ViewType switch goes to build.... depending on type of view desired
                specific view creates view,
                invokes headers to create correct view column headers
                builds an ArrayList of lines to fill out model.tableList
     */


    String fixRoadName(DecoderEntry entry) {
        String title = entry.roadName + entry.roadNumber
        if (title.isBlank()) {
            title = entry.dccAddress
        }
        return title
    }

    void doColumnHeaders(List<DecoderEntry> entries, ViewType listType) {
        if (listType == ViewType.CV_DIFF_ALL ||
                listType == ViewType.CV_DIFF_CHANGED ||
                listType == ViewType.LABELS_DIFF_ALL ||
                listType == ViewType.LABELS_DIFF_CHANGED ||
                listType == ViewType.KEY_DIFF_ALL ||
                listType == ViewType.KEY_DIFF_CHANGED) {
            doHeaderDiffs(entries, listType)
        } else {
            entries.each {
                String title = fixRoadName(it)
                model.columnNames.add(title)
                model.tableClasses.add(String.class)
            }
            restDecoderDown(entries, listType)
        }
    }

    void doHeaderDiffs(List<DecoderEntry> entries, ViewType listType) {
        String titleBase = ""
        HashSet<String> keyCollection = new HashSet<>()
        switch (listType) {
            case ViewType.CV_DIFF_ALL:
            case ViewType.CV_DIFF_CHANGED:
                model.columnNames.add("CV Number")
                titleBase = "\nCV Value"
                break
            case ViewType.LABELS_DIFF_ALL:
            case ViewType.LABELS_DIFF_CHANGED:
                model.columnNames.add("Function\nNumber")
                titleBase = "\nLabel"
                break
            case ViewType.KEY_DIFF_ALL:
            case viewType.KEY_DIFF_CHANGED:
                model.columnNames.add("Key Name")
                titleBase = "\nKey Value"
                break
            default:
                log.error("unrecognized ViewType (${listType}) in doHeaderDiffs")
        }
        log.debug("There are ${entries.size()} decoders to process for change type ${listType}")
        entries.each { decoder ->
            List<VersionBase> theVersions
            List<AbstractItem> theItems
            switch (listType) {
                case ViewType.CV_DIFF_ALL:
                case ViewType.CV_DIFF_CHANGED:
                    theVersions = decoder.cvVersions
                    theItems = decoder.cvValues
                    break
                case ViewType.LABELS_DIFF_ALL:
                case ViewType.LABELS_DIFF_CHANGED:
                    theVersions = decoder.labelVersions
                    theItems = decoder.labelValues
                    break
                case ViewType.KEY_DIFF_ALL:
                case viewType.KEY_DIFF_CHANGED:
                    theVersions = decoder.keyVersions
                    theItems = decoder.keyPairs
                    break
                default:
                    log.error("unrecognized ViewType (${listType}) in doHeaderDiffs")
            }
            decoder.metaClass.theVersions = theVersions
            theVersions.each {
                String theValue = fixRoadName(decoder) + "${it.versionNumber}\n${it.createdOn}"
                if (listType.equals(ViewType.LABELS_DIFF_ALL) || listType.equals(ViewType.LABELS_DIFF_CHANGED)) {
                    model.columnNames.add(theValue)
                    model.tableClasses.add(String.class)
                    theValue = theValue + "\nLocked?"
                    model.columnNames.add(theValue)
                    model.tableClasses.add(Boolean.class)
                } else {
                    model.columnNames.add(theValue)
                    model.tableClasses.add(String.class)
                }
            }
            model.columnNames.add(fixRoadName(decoder) + titleBase)
            model.tableClasses.add(String.class)
            ArrayList<ArrayList<String>> lines = new ArrayList<>()
            Hashtable<Integer, VersionBase> versionHash
            Hashtable<String, AbstractItem> itemHash = new Hashtable<>()
            theItems.each {
                keyCollection.add(it.getKey())
                if (theVersions != null) {
                    versionHash = new Hashtable()
                    theVersions.each({
                        versionHash.put(it.versionNumber, it)
                    })
                }
                it.metaClass.versionHash = versionHash
                itemHash.put(it.getKey(), it)
            }
            decoder.metaClass.itemHash = itemHash
        }
        log.debug("completed header - now build key list")
        boolean useCVcomparator = false
        if (viewType.equals(ViewType.CV_DIFF_ALL) || viewType.equals(ViewType.CV_DIFF_CHANGED)) {
            useCVcomparator = true
        }
        CvNameComparator comparator = new CvNameComparator()
        List<String> sortedList = keyCollection.stream()
                .sorted(useCVcomparator ? comparator : Comparator.naturalOrder())
                .toList()
        log.debug("there are ${sortedList.size()} in the key list - the number of rows (+1)")
        ArrayList<ArrayList<String>> allLines = new ArrayList<>()
        boolean addLatching = (viewType.equals(ViewType.LABELS_DIFF_ALL)) || viewType.equals(ViewType.LABELS_DIFF_CHANGED)
        sortedList.each { String keyValue ->
            ArrayList<String> thisLine = new ArrayList<>()
            thisLine.add(keyValue)
            entries.each { DecoderEntry decoder ->
                AbstractItem thisItem = decoder.itemHash.get(keyValue)
                decoder.theVersions.each { VersionBase thisVersion ->
                    if (thisItem != null) {
                        AbstractDiff thisDiff = thisItem.versionHash.get(thisVersion.versionNumber)
                        thisLine.add(thisDiff != null ? thisDiff.oldValue : "")
                        if (addLatching) {
                            thisLine.add(((LabelDiff) thisItem).oldLocked ? "Latching" : "Non-Latching")
                        }
                    } else {
                        thisLine.add("")
                        if (addLatching) {
                            thisLine.add("")
                        }
                    }
                }
                thisLine.add("")
            }
            allLines.add(thisLine)
        }
        log.debug("changed items - complete array now built - adding to tableList")
        model.tableList.addAll(allLines)
        SwingUtilities.invokeLater {
            log.debug("build changed items now invoking view.init")
            view.init()
        }
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
            if (viewType == ViewType.SPEED_PROFILE) {
                Integer speedStep = Integer.valueOf(keyVal)
                BigDecimal correctedStep = new BigDecimal(speedStep)
                correctedStep = correctedStep / 1000
                Integer step = 126 * correctedStep
                thisLine.add(String.format("%.3f", correctedStep))
                thisLine.add(step.toString())
            } else {
                thisLine.add(keyVal)
            }

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
        model.tableClasses.add(Integer.class)
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
        model.tableClasses.add(Integer.class)
        model.columnNames.add("DCC Address")
        model.tableClasses.add(Integer.class)
        newCvList.each {
            model.columnNames.add(it)
            model.tableClasses.add(Integer.class)
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
            model.tableClasses.add(Integer.class)
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
        model.tableClasses.add(Integer.class)
        doColumnHeaders(decs, ViewType.FUNCTION_LABELS)
    }

    void buildSpeedProfile() {
        view = new DataView(parent, this, model, "Speed Profiles", "speedview")
        log.debug("creatinga list of speed profile values for decoders")
        List<DecoderEntry> decs = database.getList(ViewDbService.ListType.SPEED_LIST, decoderIds, null)
        model.columnNames.add("Throttle\nPercentage")
        model.tableClasses.add(BigDecimal.class)
        model.columnNames.add("Speed\nStep")
        model.tableClasses.add(Integer.class)
        decs.each {
            String hdr = fixRoadName(it)
            model.columnNames.add(hdr + "\n" + "Forward")
            model.tableClasses.add(Double.class)
            model.columnNames.add(hdr + "\n" + "Reverse")
            model.tableClasses.add(Double.class)
        }
        restDecoderDown(decs, ViewType.SPEED_PROFILE)
    }

    void buildKeyPairs() {
        view = new DataView(parent, this, model, "Key Value Pairs View", "keyview")
        log.debug("creating a list of key value pairs for decoders")
        List<DecoderEntry> decs = database.getList(ViewDbService.ListType.KEY_VAL_LIST,
                decoderIds,
                null)
        model.columnNames.add("Key")
        model.tableClasses.add(String.class)
        doColumnHeaders(decs, ViewType.KEY_PAIRS)
    }


    /**
     *
     * @param thisType ViewType (enum) determining the type of view
     * @param dialogTitle title for the Dialog frame
     * @param viewName name of the view (for use in properties saving and retrieving)
     * @param selectType type of selection for database call
     * @param diffType either ALL or only changed rows
     * @param comparator a comparator for the keys, or null for native
     * @param getHash a Closure that returns the correct type of Hashtable
     * @param putHash a Closure that adds the correct item type to the hashtable
     */
    void allDiffBuild(ViewType thisType,
                      String dialogTitle,
                      String viewName,
                      SelectType selectType,
                      DiffType diffType,
                      Comparator<Object> comparator,
                      Closure<Hashtable<Object, Object>> getHash,
                      Closure putHash
    ) {
        log.debug("building views for ${thisType} with selections of ${selectType}")
        view = new DataView(parent,
                this,
                model,
                dialogTitle,
                viewName)
        List<DecoderEntry> decoders = database.getDecDiffs(selectType,
                diffType,
                (List) decoderIds)
        List<Object> keyList = BuildKeyList.buildList(decoders as List<Object>,
                null,
                BuildKeyList.retrieveVersions,
                BuildKeyList.retrieveDiffList,
                BuildKeyList.getKey,
                comparator,
                false,
                getHash,
                putHash)
        keyList = BuildKeyList.buildList(decoders as List<Object>,
                keyList,
                BuildKeyList.retrieveValues,
                null,
                BuildKeyList.getKey,
                comparator,
                true,
                getHash,
                putHash)
        doDiffColumnHeaders(thisType, decoders, keyList)
    }

    void cvDiffBuild(ViewType thisType,
                     String dialogTItle,
                     String viewName,
                     SelectType selectType,
                     DiffType diffType) {
        allDiffBuild(thisType,
                dialogTItle,
                viewName,
                selectType,
                diffType,
                new CvNameComparator(),
                BuildKeyList.getCVHash,
                BuildKeyList.putCVHash)
    }

    void buildCvDiffAll() {
        cvDiffBuild(ViewType.CV_DIFF_ALL,
                "CV Changes for all CVs",
                "cvdiffall",
                SelectType.SELECT_ALL_CVS,
                DiffType.ALL_VALUES
        )
    }

    void buildCvDiff() {
        cvDiffBuild(ViewType.CV_DIFF_CHANGED,
                "CV Changes for changed CVs",
                "cvdiff",
                SelectType.SELECT_ALL_CVS,
                DiffType.ONLY_CHANGED
        )
    }

    void allKeyDiffBuild(ViewType viewType, DiffType diffType) {
        allDiffBuild(viewType,
                "Key Changes for All keys",
                "keydiffall",
                SelectType.SELECT_KEY,
                diffType,
                null,
                BuildKeyList.getKeyHash,
                BuildKeyList.putKeyHash
        )
    }

    void buildKeyDiffAll() {
        allKeyDiffBuild(ViewType.KEY_DIFF_ALL, DiffType.ALL_VALUES)
    }

    void buildKeyDiff() {
        allKeyDiffBuild(ViewType.KEY_DIFF_CHANGED, DiffType.ONLY_CHANGED)
    }

    void doDiffColumnHeaders(ViewType listType,
                             List<DecoderEntry> entries,
                             List<String> keys) {
        log.debug("Processing column headers for ${entries.size()} with ${keys.size()} keys")
        String titleBase
        switch (listType) {
            case ViewType.CV_DIFF_ALL:
            case ViewType.CV_DIFF_CHANGED:
                model.columnNames.add("CV Number")
                model.tableClasses.add(Integer.class)
                titleBase = "\nCV Value"
                break
            case ViewType.LABELS_DIFF_ALL:
            case ViewType.LABELS_DIFF_CHANGED:
                model.columnNames.add("Function\nNumber")
                model.tableClasses.add(Integer.class)
                titleBase = "\nLabel"
                break
            case ViewType.KEY_DIFF_ALL:
            case viewType.KEY_DIFF_CHANGED:
                model.columnNames.add("Key Name")
                model.tableClasses.add(String.class)
                titleBase = "\nKey Value"
                break
            default:
                log.error("unrecognized ViewType (${listType}) in doHeaderDiffs")
        }
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm:ss a")
        entries.each { DecoderEntry thisEntry ->
            String columnHeader = ""
            thisEntry.versions.each { VersionBase thisVersion ->
                LocalDateTime localTime = thisVersion.createdOn.toLocalDateTime()
                String theValue = fixRoadName(thisEntry) +
                        "\nVersion: ${thisVersion.versionNumber}\n${localTime.format(dateFormat)}\n${localTime.format(timeFormat)}"
                model.columnNames.add(theValue)
                model.tableClasses.add(String.class)
                if (viewType.equals(ViewType.LABELS_DIFF_CHANGED) ||
                        viewType.equals(ViewType.LABELS_DIFF_ALL)) {
                    model.columnNames.add("Locked?")
                    model.tableClasses.add(Boolean.class)
                    columnHeader = "Function Label"
                } else if (viewType.equals(ViewType.CV_DIFF_CHANGED) ||
                        viewType.equals(ViewType.CV_DIFF_ALL)) {
                    columnHeader = "CV Value"
                } else {
                    columnHeader = "Key Value"
                }
            }
            model.columnNames.add("${fixRoadName(thisEntry)}\n${columnHeader}")
            model.tableClasses.add(String.class)
        }
        doDiffColumnValues(entries, keys)
    }

    void doDiffColumnValues(List<DecoderEntry> entries, List<String> keyList) {
        ArrayList<ArrayList<String>> allLines = new ArrayList<>()
        log.debug("there will be ${keyList.size()} lines of output")
        keyList.each { String key ->
            ArrayList<String> thisLine = new ArrayList<>()
            thisLine.add(key)
            entries.each { DecoderEntry thisEntry ->
                thisEntry.versions.each { VersionBase current ->
                    AbstractDiff thisDiff = current.keyValues.get(key)
                    thisLine.add(thisDiff ? thisDiff.oldValue : "")
                    if (viewType.equals(ViewType.LABELS_DIFF_CHANGED) ||
                            viewType.equals(ViewType.LABELS_DIFF_ALL)) {
                        thisLine.add(thisDiff ? (((LabelDiff) thisDiff).oldLocked ? "L" : "") : "")
                    }
                }
                AbstractItem thisItem = thisEntry.keyValues.get(key)
                thisLine.add(thisItem ? thisItem.getValue() : "")
            }
            allLines.add(thisLine)
        }
        model.tableList.addAll(allLines)
        SwingUtilities.invokeLater {
            log.debug("build changed items now invoking view.init")
            view.init()
        }
    }

    void allLabelDiffBuild(ViewType viewType, DiffType diffType) {
        allDiffBuild(ViewType.LABELS_DIFF_ALL,
                "Function Label Changes for All Functions",
                "labelDiffAll",
                SelectType.SELECT_FUNC,
                DiffType.ALL_VALUES,
                new StringIntegerComparator(),
                BuildKeyList.getLabelHash,
                BuildKeyList.putLabelHash)
    }

    void buildLabelDiffAll() {
        allLabelDiffBuild(ViewType.LABELS_DIFF_ALL, DiffType.ALL_VALUES)
    }

    void buildLabelDiff() {
        allLabelDiffBuild(ViewType.LABELS_DIFF_CHANGED, DiffType.ONLY_CHANGED)
    }

    def printSAction = { ActionEvent e ->
        log.debug("Print requested")
        SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yy")
        MessageFormat header = new MessageFormat(printTitle)
        MessageFormat footer = new MessageFormat("Page {0,number} Printed " + sdf.format(new Date()))
        model.theTable.print(JTable.PrintMode.FIT_WIDTH, header, footer)
    }

    def closeAction = { ActionEvent e ->
        log.debug("Close action has bee requested")
        model.dialog.setVisible(false)
    }

    def helpActiom = { ActionEvent e ->
        log.debug("help action requested")
    }
}
