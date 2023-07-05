package com.spw.rr

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor

import javax.swing.JTable
import javax.swing.SwingConstants
import javax.annotation.Nonnull
import java.awt.BorderLayout
import java.awt.Dimension

@ArtifactProviderFor(GriffonView)
class DecoderView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder
    @MVCMember @Nonnull
    DecoderModel model

    RRTableModel tableModel
    def completeTable

    void initUI() {
        JTable theTable
        RRTableModel modelCopy
        builder.with {
            application(size: [600, 400], id: 'mainWindow',
                    title: 'Decoder List') {

                menuBar() {
                    menu(text: 'File') {
                        menuItem importAction
                        menuItem exitAction

                    }
                    menu(text: 'Edit') {
                        menuItem prefsAction
                    }
                    menu(text: 'View') {

                    }
                    menu(text: 'Help') {
                        menuItem helpAction
                        menuItem aboutAction
                    }

                }
                migLayout(constraints: 'fill')
                scrollPane(constraints: BorderLayout.CENTER) {
                    theTable = table(model: modelCopy = new RRTableModel(model))
                    log.debug("theTable is ${theTable}")
                    log.debug("modelCopy is ${modelCopy}")
                    tableModel = modelCopy
                }
            }
        }
        tableModel = modelCopy
        completeTable = theTable
        theTable.setPreferredSize(new Dimension(1500, 600))
    }
}