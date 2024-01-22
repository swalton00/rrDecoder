package com.spw.rr

import griffon.core.GriffonApplication
import griffon.core.RunnableWithArgs
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import com.spw.rr.PropertiesService


import javax.annotation.Nonnull
import javax.inject.Inject
import javax.swing.JFileChooser
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.sql.Connection

@ArtifactProviderFor(GriffonController)
class PrefsController {

    @MVCMember @Nonnull
    PrefsModel model

    @Inject
    private
    PropertiesService props

    private boolean initialized = false

    void onWindowShown(String name, Object window) {
        if (name.equals("prefs")) {
            if (!initialized) {
                initialized = true
                application.eventRouter.addEventListener("prefsChange",
                        { argVal ->
                            log.debug("received message - argument is ${argVal}")
                            prefChange(argVal[0])
                        } as RunnableWithArgs
                )
            }
            log.debug("our window is being shown")
            model.username = props.getPropertyUser()
            model.password = props.getPropertyPassword()
            model.url = props.getPropertyURL()
            if (model.url.startsWith("jdbc:h2:file:")) {
                String tempString = model.url.substring("jdbc:h2:file:".length())
                if (tempString.contains(";")) {
                    tempString=tempString.substring(0,tempString.indexOf(';'))
                }
                int lastPos = tempString.lastIndexOf("\\")
                if (lastPos == -1) {
                    lastPos = tempString.lastIndexOf("/")
                }
                model.dbName = tempString.substring(lastPos + 1)
                File tempFile = new File(tempString.substring(0, lastPos))
                model.fileName = tempFile.toString()
            }
        }

    }

    void onShutdownStart(GriffonApplication application) {
        log.debug("in shutdown start for the controller")
    }

    private void closeThisWindow() {
        application.getWindowManager().hide("prefs")
    }

    @ControllerAction
    void directoryAction() {
        JFileChooser fc = new JFileChooser()
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
        File currentLocation = model.fileName
        fc.setCurrentDirectory(currentLocation.getAbsolutePath())
        fc.setSelectedFile(currentLocation)
        int result = fc.showDialog(null, "Select Location")
        if (result == JFileChooser.APPROVE_OPTION ) {
            model.prefsChanged = true
            File newLocation = fc.getSelectedFile()
            model.fileName = newLocation.toString()
        }
    }

    @ControllerAction
    void okayAction() {
        props.storeUser(model.username)
        props.storePassword(model.password)
        props.storeURL(model.url)
        props.saveProperties()
        closeThisWindow()
    }

    @ControllerAction
    void cancelAction() {
        closeThisWindow()
    }

    private breakdownURL() {

    }

    private validateSettings() {

    }

    void prefChange(String prefName) {
        log.debug("checking the change for ${prefName}")
    }

}
