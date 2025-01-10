package com.spw.rr.models

import com.spw.rr.controllers.MainController
import com.spw.rr.views.MainView
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.swing.JFrame
import javax.swing.JTable

class MainModel {

    private static final Logger log = LoggerFactory.getLogger(MainModel.class)
    MainController controller
    MainView view
    Vector<String> dataList
    JTable theTable
    JFrame baseFrame
    public MainModel() {

    }

    public MainModel(MainController controller) {
        this.controller = controller
    }

    void init() {
        log.debug("initializing the main model")
    }



}
