package com.spw.rr

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor

import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.annotation.Nonnull

import java.awt.Dimension

@ArtifactProviderFor(GriffonView)
class DecoderView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder
    @MVCMember @Nonnull
    DecoderModel model

    RRTableModel tableModel

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
                        menuItem decWindowAction,text: 'List ALL Decoders'
                        menuItem decSelectedAction,text: 'List Decoders for Selection',enabled: bind {model.tableSelectionEnabled}
                    }
                    menu(text: 'Help') {
                        menuItem helpAction
                        menuItem aboutAction
                    }

                }
                migLayout(constraints: 'fill')
                def pane = scrollPane(constraints: 'grow') {
                    theTable = table(model: modelCopy = new RRTableModel(model))
                    log.debug("theTable is ${theTable}")
                    log.debug("modelCopy is ${modelCopy}")
                    tableModel = modelCopy
                }
                pane.setPreferredSize(new Dimension(1500,600))
            }
        }
        tableModel = modelCopy
        model.theTable = theTable
        theTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        theTable.setPreferredSize(new Dimension(1500, 600))
        theTable.setCellSelectionEnabled(false)
        theTable.setColumnSelectionAllowed(false)
        theTable.setRowSelectionAllowed(true)
        theTable.getSelectionModel().addListSelectionListener(new ListenerForTables(model))
        theTable.setAutoCreateRowSorter(true)
    }


}