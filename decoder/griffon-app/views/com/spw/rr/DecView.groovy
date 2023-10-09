package com.spw.rr

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor

import javax.annotation.Nonnull
import javax.swing.JTable
import javax.swing.ListSelectionModel
import java.awt.Dimension

@ArtifactProviderFor(GriffonView)
class DecView {
    @MVCMember
    @Nonnull
    FactoryBuilderSupport builder
    @MVCMember
    @Nonnull
    DecModel model

    RRTableModel tableModel
    def completeTable

    void initUI() {
        JTable theTable
        RRTableModel modelCopy
        builder.with {
            application(size: [1200, 900], id: 'decWindow',
                    title: 'Decoder List') {

                menuBar() {
                    menu(text: 'File') {
                        menuItem importDetailAction, text: 'Import Details', enabled: bind { model.importDetailEnabled }
                    }
                    menu(text: 'Window') {
                        menuItem mainAction

                    }
                    menu(text: 'View') {

                    }
                    menu(text: 'Help') {
                        menuItem helpAction
                    }

                }
                migLayout(constraints: 'fill')
                def pane = scrollPane(constraints: 'width 1200, height 900') {
                    theTable = table(model: modelCopy = new RRTableModel((RRBaseModel) model))
                    log.debug("theTable is ${theTable}")
                    log.debug("modelCopy is ${modelCopy}")
                    tableModel = modelCopy
                }
                //pane.setPreferredSize(new Dimension(1500,600))
            }
        }
        tableModel = modelCopy
        completeTable = theTable
        model.theTable = theTable
        completeTable.setAutoCreateRowSorter(true)
        theTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        theTable.setCellSelectionEnabled(false)
        theTable.setCellSelectionEnabled(false)
        theTable.setRowSelectionAllowed(true)
        theTable.getSelectionModel().addListSelectionListener(new ListenerForTables(model))
    }
}
