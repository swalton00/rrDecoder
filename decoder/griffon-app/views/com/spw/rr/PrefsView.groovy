package com.spw.rr

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor

import javax.annotation.Nonnull
import javax.swing.ButtonGroup
import javax.swing.JRadioButton
import java.awt.GridLayout

@ArtifactProviderFor(GriffonView)
class PrefsView {

    @MVCMember
    @Nonnull
    FactoryBuilderSupport builder

    @MVCMember
    @Nonnull
    private PrefsModel model

    void initUI() {
        builder.with {
            application(size: [700,400], id: 'prefs',
                    title: 'Preferences') {
                migLayout(columnConstraints: '[shrink][grow]', layoutConstraints: 'wrap 2')
                label("Username:")
                textField(columns: 10, text: bind('username', source: model, mutual: true))
                label("Password:")
                passwordField(columns: 10, text: bind('password', source: model, mutual: true))
                ButtonGroup buttonGroup = new ButtonGroup()
                JRadioButton urlButton = new JRadioButton("URL:", true)
                JRadioButton fileButton = new JRadioButton("File:")
                buttonGroup.add( urlButton )
                buttonGroup.add( fileButton)
                buttonGroup
                label("Use either url or file name:")
                def pane = panel {

                }
                pane.add(urlButton)
                pane.add(fileButton)
                label("Database name:")
                textField(columns: 16)
                label("Select directory:")
                button(directoryAction)
                label("Location:")
                textField(columns: 60)
                label("DatabaseURL:")
                textField(columns: 150, text: bind('url', source: model, mutual: true))
                button(okayAction)
                button(cancelAction)
            }
        }


    }

}
