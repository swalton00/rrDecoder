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

    String newUserid
    String newPassword
    String newURL
    String newSchema
    //Settings tempSettings


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
        settings.settingsValid = goodSettings
        if (goodSettings) {
            settings.settingsValid = true
            log.debug("settings determined to be good - saving them")
            boolean settingsChanged = false
            settings |= checkSettingsChange(settings.schema, newSchema)
            settings |= checkSettingsChange(settings.userid, newUserid)
            settings |= checkSettingsChange(settings.password, newPassword)
            settings |= checkSettingsChange(settings.url, newURL)
            settings.schema = newSchema
            settings.userid = newUserid
            settings.password = newPassword
            settings.url = newURL
            settings.saveSettings()
            saver.writeValues()
            database.dbStart(settings)
            SwingUtilities.invokeLater {
                view.thisWindow.setVisible(false)
            }
            if (!settings.databaseOpen) {
                log.debug("database is not open, completing settings")
            } else {
                if (settingsChanged) {
                    log.debug("settings have changed - will terminate to restart")
                    SwingUtilities.invokeAndWait {
                        JOptionPane.showMessageDialog(null,
                                "Settings have changed - click to terminate and restart",
                        JOptionPane.WARNING_MESSAGE)
                        System.exit(4)
                    }
                }
            }
        }
    }

    def saveAction = { ActionEvent event ->
        log.debug("save action")
        newUserid = model.fieldUserid.getText()
        newPassword = new String(model.fieldPassword.getPassword())
        newURL = model.fieldURL.getText()
        if (!model.fieldSchema.getText().isEmpty()) {
            newSchema = model.fieldSchema.getText()
        }
        worker.execute(backgroundSave)
    }
}
