package com.spw.rr.models

import com.spw.rr.controllers.PropsController
import com.spw.rr.utilities.Settings

import javax.swing.JPasswordField
import javax.swing.JRadioButton
import javax.swing.JTextField

class PropsModel {

    Settings settings = Settings.getInstance()
    PropsController controller
    PropsModel() {

    }
    PropsModel(PropsController controller) {
        this.controller = controller
    }
    void init() {

    }
    JTextField fieldUserid = new JTextField("")
    JPasswordField fieldPassword = new JPasswordField("")
    JTextField fieldURL = new JTextField("")
    JTextField fieldSchema = new JTextField("")
    JTextField fieldLocation = new JTextField("")
    JTextField fieldDbName = new JTextField("")
    JRadioButton rbFile = new JRadioButton("Location")
    JRadioButton rbUrl = new JRadioButton("URL")
}
