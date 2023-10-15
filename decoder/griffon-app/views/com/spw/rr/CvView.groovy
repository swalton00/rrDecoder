package com.spw.rr

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor

import javax.annotation.Nonnull
import javax.swing.JTable
import javax.swing.ListSelectionModel

@ArtifactProviderFor(GriffonView)
class CvView {

    @MVCMember
    @Nonnull
    FactoryBuilderSupport builder

    @MVCMember
    @Nonnull
    CvModel model

    RRTableModel tableModel
    def completeTable

    void initUI() {
        JTable theTable
        RRTableModel modelCopy
        builder.with {
            application(size: [1200,900], id: 'cvWindow', title: 'CV Values')
            menuBar() {
                menu(text: 'File') {
                    menuItem closeAction, text: 'Close'
                }
                menu(text: 'Help') {
                    menuItem helpAction, text: 'Help'
                }
            }
            migLayout(constraints: 'fill')
            def pane = scrollPane(constraints: 'width 1200, height 900') {
                theTable = table(model: modelCopy = new RRTableModel(RRBaseModel) model)
                log.debug("the table is ${theTable}")
                tableModel = modelCopy
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
