package com.spw.rr

import javax.swing.event.TableModelEvent
import javax.swing.table.AbstractTableModel

class RRTableModel extends AbstractTableModel{

    RRBaseModel model

    RRTableModel(DecoderModel baseModel) {
        super()
        model = baseModel
    }

    @Override
    String getColumnName(int col) {
        return model.columnNames[col]
    }

    @Override
    int getRowCount() {
        if (model.tableList == null) {
            return 0
        }
        return model.tableList.size()
    }

    @Override
    int getColumnCount() {
        return model.columnNames.size()
    }

    @Override
    Object getValueAt(int rowIndex, int columnIndex) {
        return model.tableList.getAt(rowIndex).getAt(columnIndex)
    }

    void dataChanged() {
        TableModelEvent event = new TableModelEvent(this)
        fireTableChanged(event)
    }
}
