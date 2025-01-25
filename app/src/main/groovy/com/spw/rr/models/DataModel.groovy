package com.spw.rr.models

import com.spw.rr.controllers.DataController
import com.spw.rr.views.DataView
import groovy.transform.ToString
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JDialog

@ToString(includeFields = true, includeNames = true, includePackage = false)
class DataModel extends RrBaseModel {

    DataController controller
    DataView view
    JDialog dialog
    private static final Logger log = LoggerFactory.getLogger(DataModel.class)
    DataModel(DataController controller) {
        this.controller = controller
    }
    void init() {

    }
}
