package com.spw.rr

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javax.swing.SwingConstants
import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonView)
class DecoderView {
    @MVCMember
    @Nonnull
    FactoryBuilderSupport builder
    @MVCMember
    @Nonnull
    DecoderModel model

    void initUI() {
        builder.with {
            application(size: [320, 420], id: 'mainWindow',
                    title: 'Decoder List') {
                borderLayout()
                menuBar {
                    menu(text: 'File') {
                        menuItem importAction
                        menuItem exitAction

                    }
                    menu(text: 'Edit') {

                    }
                    menu(text: 'View') {

                    }
                    menu(text: 'Help') {
                        menuItem helpAction
                        menuItem aboutAction
                    }
                }
            }
        }
    }
}