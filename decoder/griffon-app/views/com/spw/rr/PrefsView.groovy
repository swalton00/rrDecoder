package com.spw.rr

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Observable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Nonnull
import javax.swing.ButtonGroup
import javax.swing.JPasswordField
import javax.swing.JRadioButton
import javax.swing.JTextField
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.sql.PreparedStatement

@ArtifactProviderFor(GriffonView)
class PrefsView implements FocusListener {

    @MVCMember
    @Nonnull
    FactoryBuilderSupport builder

    @MVCMember
    @Nonnull
    private PrefsModel model

    private static final Logger log = LoggerFactory.getLogger(PrefsView.class)
    private static final String DBNAME = "dbname"
    private static final String LOCATION = "location"
    private static final String URL = "url"
    private static final String USERNAME = "username"
    private static final String PASSWORD = "password"


    String originalValue

    void initUI() {
        builder.with {
            application(size: [700, 400], id: 'prefs',
                    title: 'Preferences') {
                migLayout(columnConstraints: '[shrink][grow]', layoutConstraints: 'wrap 2')
                label("Username:")
                textField(id: 'fieldUser', columns: 10,
                        text: bind('username', source: model, mutual: true),
                name: USERNAME)
                builder.fieldUser.addFocusListener(this)
                log.debug("just added the user field")
                ButtonGroup buttonGroup = new ButtonGroup()
                JRadioButton urlButton = new JRadioButton("URL:")
                label("Password:")
                passwordField(id: "password", columns: 10,
                        text: bind('password', source: model, mutual: true),
                name: PASSWORD)
                builder.password.addFocusListener(this)
                JRadioButton fileButton = new JRadioButton("File:")
                buttonGroup.add(urlButton)
                buttonGroup.add(fileButton)
                buttonGroup
                label("Use either url or file name:")
                def pane = panel {

                }
                pane.add(urlButton)
                pane.add(fileButton)
                label("Database name:")
                textField(id: "dbname", columns: 16,
                        text: bind('dbName', source: model, mutual: true),
                name: DBNAME)
                builder.dbname.addFocusListener(this)
                label("Select directory:")
                button(directoryAction)
                label("Location:")
                textField(id: "location", columns: 60,
                        text: bind('fileName', source: model, mutual: true),
                name: LOCATION)
                builder.location.addFocusListener(this)
                label("DatabaseURL:")
                fieldURL = textField(id:"url", columns: 150,
                        text: bind('url', source: model, mutual: true),
                name: URL)
                builder.url.addFocusListener(this)
                button(okayAction, text: "Save & Close")
                button(cancelAction)
            }
        }

    }


    @Override
    void focusGained(FocusEvent e) {
        log.debug("got the focus")
        if (e.source.class == JTextField.class | e.source.class == JPasswordField) {
            originalValue = e.source.getText()
        }
    }

    @Override
    void focusLost(FocusEvent e) {
        log.debug("lost the focus")
        boolean changedValue = false
        boolean goodValue = false
        if (e.source.class == JTextField.class | e.source.class == JPasswordField) {
            if (originalValue.equals(e.source.getText())) {
                log.debug("values are the same")
            } else {
                log.debug("values have changed")
                changedValue = true
                if (e.source.getText().length() > 0) {
                    goodValue = true
                } else {
                    goodValue = false
                }
            }
        }
        String componentName = e.getSource().getName()
        if (componentName == null) {
            return
        }
        switch (componentName) {
            case USERNAME : model.goodUsername = goodValue
                break
            case PASSWORD : model.goodPassword = goodValue
                break
            case DBNAME : model.goodDbname = goodValue
                break
            case LOCATION : model.goodlocation = goodValue
                break
            case URL : model.goodurl = goodValue
                break
            default:
                log.debug("unknown component name in switch - ${componentName}")
        }
        application.eventRouter.publishEvent("prefsChange", [componentName])
    }
}
