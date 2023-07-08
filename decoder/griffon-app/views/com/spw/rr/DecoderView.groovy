package com.spw.rr

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor

import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.SwingConstants
import javax.annotation.Nonnull
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import java.awt.BorderLayout
import java.awt.Dimension

@ArtifactProviderFor(GriffonView)
class DecoderView implements ListSelectionListener{
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
        completeTable = theTable
        theTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        theTable.setPreferredSize(new Dimension(1500, 600))
        theTable.setCellSelectionEnabled(false)
        theTable.setColumnSelectionAllowed(false)
        theTable.setRowSelectionAllowed(true)
        theTable.getSelectionModel().addListSelectionListener(this)
        theTable.setAutoCreateRowSorter(true)
    }

    @Override
    void valueChanged(ListSelectionEvent e) {
        ListSelectionModel lsm = (ListSelectionModel)e.getSource()
        if (lsm.valueIsAdjusting) {
            return
        }
        model.selectedRows.clear()
        if (lsm.isSelectionEmpty()) {
            model.tableSelectionEnabled = false
        } else  {
            int min = lsm.getMinSelectionIndex()
            int max = lsm.getMaxSelectionIndex()
            for (i in 0..max) {
                if (lsm.isSelectedIndex(i)) {
                    int rowIndex = completeTable.convertRowIndexToModel(i)
                    ArrayList<String> temp = model.tableList.get(rowIndex)
                    String idString = temp.get(0)
                    int thisId = Integer.valueOf(idString)
                    model.selectedRows.add(thisId)
                }
            }
            model.tableSelectionEnabled = true
        }
    }
}