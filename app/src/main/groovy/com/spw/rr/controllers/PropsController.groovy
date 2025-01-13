package com.spw.rr.controllers

import com.spw.rr.models.PropsModel
import com.spw.rr.views.PropsView
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.awt.Window
import java.awt.event.ActionEvent

class PropsController {

    PropsModel model
    PropsView view
    Window parentWidow

    private static final Logger log = LoggerFactory.getLogger(PropsController.class)

    void init(Window parent) {
        parentWidow = parent
        model = new PropsModel(this)
        view = new PropsView(this, model, parent)
        model.init()
        view.init()
    }

    def chooseAction  = { ActionEvent event ->
        log.debug("Choosing the location for the database")
    }

    def cancelAction  = { ActionEvent event ->
        log.debug("cancel action")
        view.thisWindow.setVisible(false)
    }

    def saveAction = { ActionEvent event ->
        log.debug("save action")
    }
}
