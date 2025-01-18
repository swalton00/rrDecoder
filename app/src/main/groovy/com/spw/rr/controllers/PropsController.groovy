package com.spw.rr.controllers

import com.spw.rr.models.PropsModel
import com.spw.rr.utilities.BackgroundWorker
import com.spw.rr.utilities.DatabaseServices
import com.spw.rr.utilities.Settings
import com.spw.rr.views.PropsView
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import java.awt.Window
import java.awt.event.ActionEvent

class PropsController {
    BackgroundWorker worker = BackgroundWorker.getInstance()
    DatabaseServices database = DatabaseServices.getInstance()
    PropsModel model
    PropsView view
    Window parentWidow
    Settings settings
    Settings newSettings


    private static final Logger log = LoggerFactory.getLogger(PropsController.class)

    void init(Window parent, Settings parentSettings) {
        parentWidow = parent
        model = new PropsModel(this)
        view = new PropsView(this, model, parent)
        model.init(parentSettings)
        view.init()
    }

    def chooseAction  = { ActionEvent event ->
        log.debug("Choosing the location for the database")
        JFileChooser chooser = new JFileChooser()
        chooser.setDialogTitle("Select OpsPro Home Directory")
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
        int returnValue = chooser.showDialog(null, "Select OpsPro Home")
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File chosen = chooser.getSelectedFile()
            log.debug("selected file was ${chosen.toString()}")
            model.fieldLocation.setText(chosen.toString())
            model.combineFields()
        } else {
            log.debug("selection was canceled ")
        }
    }

    def cancelAction  = { ActionEvent event ->
        log.debug("cancel action")
        view.thisWindow.setVisible(false)
    }

    def backgroundSave = { ->
        log.debug("validating the settings in ${newSettings}")
        boolean goodSettings = database.validate(newSettings)
        if (goodSettings) {
            log.debug("settings determined to be good - saving them")
            settings = newSettings
            settings.saveSettings()
            database.dbStart(settings)
            SwingUtilities.invokeLater {
                view.thisWindow.setVisible(false)
            }
        }
    }

    def saveAction = { ActionEvent event ->
        log.debug("save action")
        newSettings = new Settings()
        newSettings.settingsComplete = false
        newSettings.settingsValid = false
        newSettings.userid = model.fieldUserid.getText()
        newSettings.password = new String(model.fieldPassword.getPassword())
        newSettings.url = model.fieldURL.getText()
        if (!model.fieldSchema.getText().isEmpty()) {
            newSettings.schema = model.fieldSchema.getText()
        }
        worker.execute(backgroundSave)
    }
}
