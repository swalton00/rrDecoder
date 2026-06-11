package com.spw.rr.controllers

import com.spw.rr.models.PropsModel
import com.spw.rr.utilities.BackgroundWorker
import com.spw.rr.utilities.DatabaseServices
import com.spw.rr.utilities.PropertySaver
import com.spw.rr.utilities.Settings
import com.spw.rr.views.PropsView
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import java.awt.Window
import java.awt.event.ActionEvent

class PropsController {
    BackgroundWorker worker = BackgroundWorker.getInstance()
    DatabaseServices database = DatabaseServices.getInstance()
    PropertySaver saver = PropertySaver.getInstance()
    PropsModel model
    PropsView view
    Window parentWidow
    Settings settings

    Settings tempSettings
    MainController parentController = null

    PropsController() {

    }

    PropsController(MainController parentController) {
        this.parentController = parentController
    }

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
        chooser.setDialogTitle("Select Database Location")
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
        int returnValue = chooser.showDialog(null, "Set DB Location")
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

    boolean checkSettingsChange(String oldSetting, String newSetting) {
        return !(oldSetting == newSetting)
    }

    def backgroundSave = { ->
        log.debug("validating the settings in ${tempSettings}")
        boolean goodSettings = database.validate(tempSettings)
        if (!goodSettings) {
            SwingUtilities.invokeLater {
                model.fieldMessages.setText(database.getErrorMessage())
            }
        }
        settings = parentController.settings
        settings.settingsValid = goodSettings
        if (goodSettings) {
            settings.settingsValid = true
            log.debug("settings determined to be good - saving them")
            boolean settingsChanged = false
            settingsChanged |= checkSettingsChange(settings.schema, tempSettings.schema)
            settingsChanged |= checkSettingsChange(settings.userid, tempSettings.userid)
            settingsChanged |= checkSettingsChange(settings.password, tempSettings.password)
            settingsChanged |= checkSettingsChange(settings.url, tempSettings.url)
            settings.schema = tempSettings.schema
            settings.userid = tempSettings.userid
            settings.password = tempSettings.password
            settings.url = tempSettings.url
            settings.saveSettings()
            saver.writeValues()
            database.dbStart(settings)
            SwingUtilities.invokeLater {
                view.thisWindow.setVisible(false)
            }
            if (!settings.settingsComplete) {
                log.debug("database is not open, completing settings")
                database.dbStart(settings)
                parentController.completeInit()
            } else {
                if (settingsChanged) {
                    log.debug("settings have changed - will terminate to restart")
                    SwingUtilities.invokeAndWait {
                        JOptionPane.showMessageDialog(parentWidow,
                                "Settings have changed - click to terminate and restart",
                                "Message",
                        JOptionPane.WARNING_MESSAGE)
                        System.exit(4)
                    }
                }
            }
        }
    }

    def saveAction = { ActionEvent event ->
        log.debug("save action")
        tempSettings = new Settings()
        tempSettings.userid = model.fieldUserid.getText()
        tempSettings.password = new String(model.fieldPassword.getPassword())
        tempSettings.url = model.fieldURL.getText()
        if (!model.fieldSchema.getText().isEmpty()) {
            tempSettings.schema = model.fieldSchema.getText()
        }
        worker.execute(backgroundSave)
    }
}
