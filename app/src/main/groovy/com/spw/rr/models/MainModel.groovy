package com.spw.rr.models

import com.spw.rr.controllers.MainController
import com.spw.rr.views.MainView
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JFrame
import javax.swing.JMenuItem
import javax.swing.JTable

class MainModel extends RrBaseModel {
    private static final Logger log = LoggerFactory.getLogger(MainModel.class)

    MainController controller
    MainView view
    JFrame baseFrame

    JMenuItem viewItem
    JMenuItem importDetailItem
    boolean elementSelected = false


    public MainModel(MainController controller) {
        this.controller = controller
    }

    void init() {
        log.debug("initializing the main model")
        columnNames.addAll(["Id", "System", "# Decoders", "Full Path", "File Date", "Import Date"])
        preferredWidths.addAll([10,20,300])

    }




}
