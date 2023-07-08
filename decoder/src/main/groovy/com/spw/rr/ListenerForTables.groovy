package com.spw.rr

import javax.swing.ListSelectionModel
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener

class ListenerForTables implements ListSelectionListener {

    RRBaseModel model

    ListenerForTables(RRBaseModel model) {
        this.model = model
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
            for (i in min..max) {
                if (lsm.isSelectedIndex(i)) {
                    int rowIndex = model.theTable.convertRowIndexToModel(i)
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
