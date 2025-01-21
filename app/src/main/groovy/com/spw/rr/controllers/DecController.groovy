package com.spw.rr.controllers

import com.spw.rr.database.DecoderEntry
import com.spw.rr.database.RosterEntry
import com.spw.rr.models.DecModel
import com.spw.rr.viewdb.ViewDbService
import com.spw.rr.views.DecView
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.awt.Component
import java.awt.event.ActionEvent

class DecController {

    private static final Logger log = LoggerFactory.getLogger(DecController.class)
    DecModel model
    DecView view
    Component parent

    ViewDbService dbService = ViewDbService.getInstance()
    List<Integer> idList

    DecController(Component parent, ArrayList<Integer> rosterIds) {
        model = new DecModel()
        model.init()
        view = new DecView(parent, this, model)
        idList = rosterIds
    }

    void init() {
        List<DecoderEntry> entries = dbService.listDecoderByRosterId(idList)
        buildList(entries)
        view.init()
    }

    void buildList(List<DecoderEntry> entries) {
        log.debug("building the display list from a list of size ${entries.size()}")
        model.tableList.clear()
        entries.each {entry ->
            ArrayList<String> nextLine = new ArrayList<>()
            nextLine.add(entry.id.toString())
            nextLine.add(entry.rosterId.toString())
            nextLine.add(entry.dccAddress)
            nextLine.add(entry.hasDetail)
            nextLine.add(entry.hasSpeedProfile)
            nextLine.add(entry.fileName)
            nextLine.add(entry.roadName)
            nextLine.add(entry.roadNumber)
            nextLine.add(entry.manufacturer)
            nextLine.add(entry.owner)
            nextLine.add(entry.dateUpdated.toString())
            model.tableList.add(nextLine)
            if (view.tableModel != null) {
                // view.tableModel will be null since we haven't done view.init yet
                view.tableModel.fireTableDataChanged()
            }
        }
    }

    def fileCloseAction = { ActionEvent e ->
        log.debug("closing the Decoder dialog")
        model.thisDialog.setVisible(false)
    }

    def importDetailAction = { ActionEvent e ->
        log.debug("Import detail action requested")
    }

    def viewSpeedProfileAction = { ActionEvent e ->
        log.debug("view Speed Profile action requested")
    }

    def viewSpeedGraphAction = { ActionEvent e ->
        log.debug("view Speed Graph action requested")
    }

    def viewDecDetailAction = { ActionEvent e ->
        log.debug("view Decoder Detail action requested")
    }

    def viewFunctionAction = { ActionEvent e ->
        log.debug("view Function action requested")
    }

    def viewSKeyPairsAction = { ActionEvent e ->
        log.debug("view Key Value Pairs action requested")
    }

    def viewStandCvAction = { ActionEvent e ->
        log.debug("view Standard CV action requested")
    }

    def viewSelCvAction = { ActionEvent e ->
        log.debug("view Selected CV action requested")
    }

    def viewAllCvAction = { ActionEvent e ->
        log.debug("view All CV action requested")
    }

    def helpAction = { ActionEvent e ->
        log.debug("Help action requested")
    }

}
