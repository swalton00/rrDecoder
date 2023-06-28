package com.spw.rr

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor

import javax.annotation.Nonnull
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
            application(size: [400,200], id: 'prefs',
                    title: 'Preferences') {
                gridLayout(new GridLayout(4, 2))
                label("Username:")
                textField(columns: 10, text: bind('username', source: model, mutual: true))
                label("Password:")
                passwordField(columns: 10, text: bind('password', source: model, mutual: true))
                label("DatabaseURL:")
                textField(columns: 150, text: bind('url', source: model, mutual: true))
                button(okayAction)
                button(cancelAction)
            }
        }


    }

}
