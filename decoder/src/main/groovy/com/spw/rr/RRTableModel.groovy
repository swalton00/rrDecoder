package com.spw.rr

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.event.TableModelEvent
import javax.swing.table.AbstractTableModel

class RRTableModel extends AbstractTableModel{

    RRBaseModel model

    private static final Logger log = LoggerFactory.getLogger(RRTableModel.class)


    RRTableModel(RRBaseModel baseModel) {
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
        int tableSize = model.tableList.size()
      //  log.trace("returning a table model size of ${tableSize}")
        return tableSize
    }

    @Override
    int getColumnCount() {
        return model.columnNames.size()
    }

    @Override
    Object getValueAt(int rowIndex, int columnIndex) {
       // log.debug("getting value at row ${rowIndex} and column ${columnIndex}")
        return model.tableList.getAt(rowIndex).getAt(columnIndex)
    }

    void dataChanged() {
        TableModelEvent event = new TableModelEvent(this)
        fireTableChanged(event)
    }
}
