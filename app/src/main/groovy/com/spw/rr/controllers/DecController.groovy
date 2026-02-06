package com.spw.rr.controllers

import com.spw.rr.database.DecoderEntry
import com.spw.rr.database.RosterEntry
import com.spw.rr.models.DecModel
import com.spw.rr.utilities.BackgroundWorker
import com.spw.rr.utilities.FrameHelper
import com.spw.rr.utilities.ImportService
import com.spw.rr.viewdb.ViewDbService
import com.spw.rr.views.DecView
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JDialog
import javax.swing.JTable
import javax.swing.SwingUtilities
import javax.swing.SwingWorker
import java.awt.Component
import java.awt.event.ActionEvent
import java.text.MessageFormat
import java.text.SimpleDateFormat

class DecController {

    private static final Logger log = LoggerFactory.getLogger(DecController.class)
    DecModel model
    DecView view

    ImportService imports = ImportService.getInstance()
    BackgroundWorker worker = BackgroundWorker.getInstance()
    ViewDbService dbService = ViewDbService.getInstance()
    List<Integer> idList

    DecController(Component parent, ArrayList<Integer> rosterIds, boolean importOkay) {
        model = new DecModel()
        model.init()
        model.selectedViewImportGood = importOkay
        view = new DecView(parent, this, model)
        idList = rosterIds
    }

    void rebuildList() {
        List<DecoderEntry> entries = dbService.listDecoderByRosterId(idList)
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater {
                buildList(entries)
            }
        } else {
            buildList(entries)
        }

    }

    void init() {
        rebuildList()
        view.init()
    }

    void buildList(List<DecoderEntry> entries) {
        log.debug("building the display list from a list of size ${entries.size()}")
        model.tableList.clear()
        entries.each {entry ->
            ArrayList<Object> nextLine = new ArrayList<>()
            nextLine.add(entry.id)
            nextLine.add(entry.rosterId)
            nextLine.add(Integer.valueOf(entry.dccAddress))
            nextLine.add(entry.hasSpeedProfile)
            nextLine.add(entry.hasDetail)
            nextLine.add(entry.fileName)
            nextLine.add(entry.roadName)
            nextLine.add(entry.roadNumber)
            nextLine.add(entry.manufacturer)
            nextLine.add(entry.decoderFamily)
            nextLine.add(entry.decoderModel)
            nextLine.add(entry.owner)
            nextLine.add(entry.dateUpdated)
            nextLine.add(entry.importDate )
            nextLine.add(entry.detailTime)
            model.tableList.add(nextLine)
            if (view.tableModel != null) {
                // view.tableModel will be null since we haven't done view.init yet
                view.tableModel.fireTableDataChanged()
            }
        }
    }

    def filePrintAction = { ActionEvent e ->
        log.debug("Print requested")
        SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yy")
        model.theTable.print(JTable.PrintMode.FIT_WIDTH, new MessageFormat("Decoder List"),
        new MessageFormat("Printed " + sdf.format(new Date())))
    }

    def fileCloseAction = { ActionEvent e ->
        log.debug("closing the Decoder dialog")
        FrameHelper.closeAction(model.thisDialog, model.theTable)
        model.thisDialog.setVisible(false)
    }

    def importDetailAction = { ActionEvent e ->
        log.debug("Import detail action requested")
        ArrayList<Integer> importRows = new ArrayList<>()
        model.selectedRows.each( {
            importRows.add(it)
        } )
        worker.execute { ->
            imports.importDetail(model.thisDialog, importRows)
            rebuildList()
        }
    }

    List<Integer> buildSelectedList() {
        log.debug("building a list from a selected list of values  ${model.selectedRows}")
        ArrayList<Integer> retList = new ArrayList<>()
        model.selectedRows.each {
            retList.add(it)
        }
        log.debug("returning a list of size ${retList.size()}")
        return retList
    }

    void doDataView(DataController.ViewType viewType, List<Integer> selList, String cvList) {
        worker.execute {
            if (viewType.equals( DataController.ViewType.SELECTED_CVS)) {
                new DataController((JDialog) model.thisDialog, viewType, selList, cvList)
            } else {
                new DataController((JDialog) model.thisDialog, viewType, selList)
            }
            log.trace("worker finished executing view std cvs")
        }
    }

    def viewSpeedProfileAction = { ActionEvent e ->
        log.debug("view Speed Profile action requested")
        List<Integer> selList = buildSelectedList()
        doDataView(DataController.ViewType.SPEED_PROFILE, selList, null)
    }

    def viewSpeedGraphAction = { ActionEvent e ->
        log.debug("view Speed Graph action requested")
        List<Integer> selList = buildSelectedList()
        worker.execute( {
            log.debug("starting a task to build a graph")
            GraphController graphController = new GraphController(model.thisDialog, selList)
        })
    }

    def viewDecDetailAction = { ActionEvent e ->
        log.debug("view Decoder Detail action requested")
        List<Integer> selList = buildSelectedList()
        doDataView(DataController.ViewType.DECODER_DETAIL, selList, null)
    }

    def viewFunctionAction = { ActionEvent e ->
        log.debug("view Function action requested")
        List<Integer> selList = buildSelectedList()
        doDataView(DataController.ViewType.FUNCTION_LABELS, selList, null)
    }

    def viewSKeyPairsAction = { ActionEvent e ->
        List<Integer> selList = buildSelectedList()
        log.debug("view Key Value Pairs action requested")
        doDataView(DataController.ViewType.KEY_PAIRS, selList, null)
    }

    def viewStandCvAction = { ActionEvent e ->
        log.debug("view Standard CV action requested")
        List<Integer> selList = buildSelectedList()
        doDataView( DataController.ViewType.STANDARD_CVS, selList, null)

    }

    def viewSelCvAction = { ActionEvent e ->
        log.debug("view Selected CV action requested")
        List<Integer> selList = buildSelectedList()
        String cvList = model.cvListField.getText()
        doDataView( DataController.ViewType.SELECTED_CVS, selList, cvList)
    }

    def viewAllCvAction = { ActionEvent e ->
        log.debug("view All CV action requested")
        List<Integer> selList = buildSelectedList()
        String cvList = model.cvListField.getText()
        doDataView( DataController.ViewType.ALL_CVS, selList, null)
    }

    def helpAction = { ActionEvent e ->
        log.debug("Help action requested")
    }

}
