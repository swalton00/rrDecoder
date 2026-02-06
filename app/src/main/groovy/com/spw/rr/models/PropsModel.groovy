package com.spw.rr.models

import com.spw.rr.controllers.PropsController
import com.spw.rr.utilities.Settings
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import java.awt.*
import java.awt.event.FocusEvent
import java.awt.event.FocusListener

class PropsModel implements FocusListener {
    private static final Logger log = LoggerFactory.getLogger(PropsModel.class)
    Settings settings
    String oldValue = ""
    PropsController controller

    PropsModel() {

    }

    PropsModel(PropsController controller) {
        this.controller = controller
    }

    void init(Settings oldSettings) {
        settings = oldSettings
    }
    JTextField fieldUserid = new JTextField("")
    JPasswordField fieldPassword = new JPasswordField("")
    JTextField fieldURL = new JTextField("")
    JTextField fieldSchema = new JTextField("")
    JTextField fieldLocation = new JTextField("")
    JTextField fieldDbName = new JTextField("")
    JRadioButton rbFile = new JRadioButton("Location")
    JRadioButton rbUrl = new JRadioButton("URL")
    JButton buttonCancel = new JButton("Cancel")
    JButton buttonSave = new JButton("Save new settings")

    void checkComplete() {
        if (!(fieldUserid.getText().isEmpty()
                | (new String(fieldPassword.getPassword()).isEmpty()
                | fieldURL.getText().isEmpty()))) {
            buttonSave.setEnabled(false)
        } else {
            buttonSave.setEnabled(true)
        }
    }

    void combineFields() {
        if (!(fieldLocation.getText().isEmpty()
                | fieldDbName.getText().isEmpty()
                | fieldSchema.getText().isEmpty())) {
            fieldURL.setText(
                    "jdbc:h2:file:" +
                            fieldLocation.getText() +
                            "/" +
                            fieldDbName.getText() +
                            ";AUTO_SERVER=TRUE;"
            )
            rbFile.setSelected(true)
            log.debug("combined fields into ${fieldURL.getText()}")
        }

    }

    @Override
    void focusGained(FocusEvent e) {
        log.trace("focus gained - ${e}")
        if (e.component.getName().equals("password")) {
            oldValue = new String(e.getComponent().getPassword())
        } else {
            oldValue = e.getComponent().getText()
        }
    }

    @Override
    void focusLost(FocusEvent e) {
        log.trace("focus lost - ${e}")
        Component comp = e.getComponent()
        String newValue
        if (e.getComponent().getName().equals("password")) {
            newValue = new String(((JPasswordField) comp).getPassword())
        } else {
            newValue = comp.getText()
        }

        if (comp.getName().equals("location")
                | comp.getName().equals("database")
                | comp.getName().equals("schema")) {
            if (!(fieldLocation.getText().isEmpty() |
                    fieldSchema.getText().isEmpty() |
                    fieldDbName.getText().isEmpty())) {
                log.debug("combining fields into URL")
                combineFields()
            }
        }
        String t1 = fieldUserid.getText()
        String t2 = new String(fieldPassword.getPassword())
        String t3 = fieldURL.getText()
        String t4 = fieldSchema.getText()
        boolean t1flag = fieldUserid.getText().isEmpty()
        boolean t2flag = fieldPassword.getPassword().size() > 0
        if (!(fieldUserid.getText().isEmpty() |
                fieldPassword.getPassword().size() == 0 |
                fieldURL.getText().isEmpty())) {
            buttonSave.setEnabled(true)
            log.trace("save button is now enabled")
        } else {
            buttonSave.setEnabled(false)
        }
        if (oldValue.equals(newValue)) {
            return
        }
    }
}
