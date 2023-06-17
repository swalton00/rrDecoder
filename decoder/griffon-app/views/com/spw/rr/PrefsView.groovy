package com.spw.rr

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javax.swing.SwingConstants
import javax.annotation.Nonnull
import java.awt.BorderLayout

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
            application(size: [320, 420], id: 'prefs',
                    title: 'Preferences') {
                migLayout()
                label("Username:", constraints: 'left')
                textField(columns: 10, text: bind('username', target: model, mutual: true), constraints: 'wrap')
                label("Password:", constraints: 'left')
                passwordField(columns: 10, text: bind('password', target: model, mutual: true), constraints: 'wrap')
                label("DatabaseURL:", constraints: 'left')
                textField(columns: 150, text: bind('url', target: model, mutual: true), constraints: 'wrap')
            }
        }


    }

}
