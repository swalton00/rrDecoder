package com.spw.rr.views

import com.spw.rr.controllers.PropsController
import com.spw.rr.models.PropsModel
import com.spw.rr.utilities.FrameHelper
import net.miginfocom.swing.MigLayout

import javax.swing.ButtonGroup
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import java.awt.Window

class PropsView {

    Window parent
    Window thisWindow

    PropsController controller
    PropsModel model

    PropsView() {

    }
    PropsView(PropsController controller, PropsModel model, Window parent) {
        this.parent = parent
        this.controller = controller
        this.model = model
    }

    void breakdownURL(String url) {
        log.debug("breaking down the url ${url} into its components")
        String schema
        String dbname
        boolean autoserver = false
        String temp = url
        if (url.contains(";SCHEMA=")) {
            String schTemp = temp.substring(0, temp.indexOf(";SCHEMA="))
            log.debug("front part of schema is ${schTemp}")
            String schBack = temp.substring(temp.indexOf(";SCHEMA=") + ";SCHEMA=".size())
            log.debug("back part of schema is ${schBack}")
        }

    }

    void init() {
        thisWindow = new JDialog(parent, "Decoder Preferences")
        thisWindow.setName("prefs")
        thisWindow.addComponentListener(new FrameHelper())
        thisWindow.getContentPane().setLayout(new MigLayout())
        if (model.settings.userid != null) {
            model.fieldUserid.setText( model.settings.userid)
        }
        if (model.settings.password != null) {
            model.fieldPassword.setText(model.settings.password)
        }
        if (model.settings.url != null) {
            model.fieldURL.setText(model.settings.url)
        }
        if (model.settings.schema != null) {
            model.fieldSchema.setText(model.settings.schema)
        }
        JLabel labelUserid = new JLabel("Userid:")
        thisWindow.getContentPane().add(labelUserid, "right")
        model.fieldPassword.setColumns(8)
        model.fieldUserid.setColumns(8)
        thisWindow.add(model.fieldUserid, "left, wrap")
        JLabel labelPassword = new JLabel("Password:")
        thisWindow.getContentPane().add(labelPassword, "right")
        thisWindow.getContentPane().add(model.fieldPassword ,"left, wrap")
        ButtonGroup rbGroup = new ButtonGroup()
        rbGroup.add(model.rbFile)
        rbGroup.add(model.rbUrl)
        model.rbUrl.setSelected(true)
        thisWindow.getContentPane().add(model.rbFile, "center")
        thisWindow.getContentPane().add(model.rbUrl, "center, wrap")
        JLabel labelUrl = new JLabel("URL:")
        thisWindow.getContentPane().add(labelUrl, "right")
        thisWindow.getContentPane().add(model.fieldURL, "left, wrap")
        model.fieldURL.setColumns(30)
        JPanel locPanel = new JPanel(new MigLayout())
       JLabel labelLoc = new JLabel("Database Location:")
        locPanel.add(labelLoc, "right")
        model.fieldLocation.setColumns(20)
        locPanel.add(model.fieldLocation, "left, wrap")
        JButton fileButton = new JButton("Choose Location")
        locPanel.add(fileButton, "center")
        thisWindow.getContentPane().add(locPanel, "span 2, wrap")
        JLabel labelDB = new JLabel("Database Name:")
        thisWindow.getContentPane().add(labelDB, "right")
        model.fieldDbName.setColumns(10)
        thisWindow.getContentPane().add(model.fieldDbName, "left, wrap")
        JButton buttonCancel = new JButton("Cancel")
        thisWindow.getContentPane().add(buttonCancel)
        buttonCancel.addActionListener(controller.cancelAction)
        JButton buttonSave = new JButton("Save new settings")
        thisWindow.getContentPane().add(buttonSave, "wrap")
        buttonSave.addActionListener(controller.saveAction)
        FrameHelper.setFrameValues(thisWindow, "prefs", 500, 400)
        thisWindow.pack()
        thisWindow.setVisible(true)
    }

}
