package com.spw.rr.utilities

import com.spw.rr.models.RrBaseModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JTable
import javax.swing.table.AbstractTableModel

class RrTableModel extends AbstractTableModel{
    private static final Logger log = LoggerFactory.getLogger(RrTableModel.class)
    RrBaseModel model
    ArrayList<Class> tableClasses = new ArrayList()

    void setClasses(ArrayList<Class> newClasses) {
        tableClasses.clear()
        for (i in 0..<newClasses.size()) {
            tableClasses.add(newClasses(i))
        }
    }

    RrTableModel(RrBaseModel model) {
        super()
        this.model = model
    }

    @Override
    String getColumnName(int col) {
        return model.columnNames.get(col)
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

    @Override
    Class getColumnClass(int columnNumber) {
        if (columnNumber < tableClasses.size()) {
            log.debug("returning tableClass for column ${columnNumber} - ${tableClasses.get(columnNumber)}")
            return (Class)tableClasses.get(columnNumber)
        } else {
            return String.class
        }
   }

    public static ArrayList<Integer> getSelectedRows(JTable theTable) {
        ArrayList<Integer> returnValue = new ArrayList()
        if (theTable.getSelectedRowCount() == 0) {
            log.debug("no selected rows - returning an empty Array")
            return returnValue
        }
        int[] selected = theTable.getSelectedRows()
        for (i in 0..<selected.size()) {
            int newValue = theTable.convertRowIndexToModel(selected[i])
            returnValue.add((Integer)newValue)
        }
        return returnValue
    }
}
