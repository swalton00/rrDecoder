package com.spw.rr

import com.spw.rr.database.DbTools
import griffon.core.GriffonApplication
import griffon.core.RunnableWithArgs
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import com.spw.rr.PropertiesService
import jdk.nashorn.internal.scripts.JO

import javax.annotation.Nonnull
import javax.inject.Inject
import javax.swing.JDialog
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.sql.Connection

@ArtifactProviderFor(GriffonController)
class PrefsController {

    @MVCMember
    @Nonnull
    PrefsModel model

    @Inject
    private
    PropertiesService props

    private boolean initialized = false

    Object mywindow = null

    void onWindowShown(String name, Object window) {
        if (name.equals("prefs")) {
            if (!initialized) {
                initialized = true
                mywindow = window
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
            if (model.username.length() >0 ) {
                model.goodUsername = true
            }
            if (model.password.length() > 0)
                model.goodPassword = true
            if (model.url.length() > 0 ) {
                model.goodurl = true
            }
            breakdownURL()
            if (model.fileName.length() > 0) {
                model.goodlocation = true
            }
            if (model.dbName.length() > 0) {
                model.goodDbname = true
            }
            validateSettings()
            if (model.okayEnabled) {
                model.propertiesValid = true
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
        if (model.fileName.length() > 0) {
            log.debug("using previous location as starting point for directory")
            File currentLocation = new File(model.fileName)
            fc.setCurrentDirectory(currentLocation.getAbsolutePath())
            fc.setSelectedFile(currentLocation)
        } else {
            log.debug("no previous directiory - using default for current")
        }
        int result = fc.showDialog(null, "Select Location")
        if (result == JFileChooser.APPROVE_OPTION) {
            model.prefsChanged = true
            File newLocation = fc.getSelectedFile()
            model.fileName = newLocation.toString()
            model.goodlocation = true
            buildURL()
            validateSettings()
        } else {
            log.debug("didn't get an approve - not changing values")
        }
    }

    @ControllerAction
    void okayAction() {
        DbTools dbTools = DbTools.getInstance()
        String returnedMessage = dbTools.checkUrl(model.url, model.username, model.password)
        if (returnedMessage.length() > 0) {
            model.errorMessage = returnedMessage
            return
        }
        props.storeUser(model.username)
        props.storePassword(model.password)
        props.storeURL(model.url)
        props.saveProperties()
        if (model.prefsChanged) {
            int retValue = JOptionPane.showConfirmDialog(mywindow,
                    "Press OKAY to close -- database options have changed",
                    "RrDecoder Restart message",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE)
            if (retValue == JOptionPane.OK_OPTION ) {
                application.shutdown()
            }
        }
        closeThisWindow()
    }

    @ControllerAction
    void cancelAction() {
        closeThisWindow()
    }

    private breakdownURL() {
        if (model.url.startsWith("jdbc:h2:file:")) {
            String tempString = model.url.substring("jdbc:h2:file:".length())
            if (tempString.contains(";")) {
                tempString = tempString.substring(0, tempString.indexOf(';'))
            }
            int lastPos = tempString.lastIndexOf("\\")
            int otherLast = tempString.lastIndexOf("/")
            if (lastPos == -1 | otherLast > lastPos) {
                lastPos = tempString.lastIndexOf("/")
            }
            model.dbName = tempString.substring(lastPos + 1)
            File tempFile = new File(tempString.substring(0, lastPos))
            model.fileName = tempFile.toString()
        }
    }

    private buildURL() {
        if (model.goodlocation & model.goodDbname) {
            model.url = "jdbc:h2:file:" + model.fileName + "/" + model.dbName + ";AUTO_SERVER=TRUE;MODE=DB2;"
            model.goodurl = true
            log.debug("url built as ${model.url}")
        }

    }

    private validateSettings() {
        if (model.goodUsername & model.goodPassword & model.goodurl) {
            model.okayEnabled = true
        }
    }

    void prefChange(String prefName) {
        log.debug("checking the change for ${prefName}")

        if (model.goodurl & prefName.equals("url")) {
            log.debug("checking for valid items in URL")
            if (model.url.startsWith("jdbc:h2")) {
                breakdownURL()
            }
        }
        if ((model.goodlocation & model.goodDbname) & (prefName.equals("location") | prefName.equals("dbname"))) {
            buildURL()
        }
        validateSettings()
    }

}
