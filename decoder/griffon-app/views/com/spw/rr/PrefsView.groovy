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
            application(size: [320, 420], id: 'prefsWindow',
                    title: 'Preferences') {
                gridLayout(rows: 10, columns: 2)
            }
        }


    }

}
