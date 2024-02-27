package com.spw.rr


import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonView)
class CvSpecificView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder
    @MVCMember @Nonnull
    CvSpecificModel model

    RRTableModel tableModel

    def completeTable

    private static final Logger log = LoggerFactory.getLogger(CvSpecificView.class)

    void initUI() {
        JTable theTable
        RRTableModel modelCopy
        builder.with {
            application(size: [1200, 900], id: 'cvSpecific') {
                menuBar() {
                    menu(text: 'Help') {
                        menuItem helpAction
                    }
                }
                migLayout(constraints: 'fill')
                def pane = scrollPane(constraints: 'width 1200, height 900, wrap') {
                    theTable = table(model: modelCopy = new RRTableModel((RRBaseModel) model))
                    tableModel = modelCopy
                }
            }
        }
        tableModel = modelCopy
        completeTable = theTable
        model.theTable = theTable
        theTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        theTable.setCellSelectionEnabled(false)
        theTable.setCellSelectionEnabled(false)
        theTable.setRowSelectionAllowed(true)
    }
}