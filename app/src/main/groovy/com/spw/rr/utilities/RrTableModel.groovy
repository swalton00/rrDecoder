package com.spw.rr.utilities

import com.spw.rr.models.RrBaseModel

import javax.swing.table.AbstractTableModel

class RrTableModel extends AbstractTableModel{
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
            return (Class)tableClasses.get(columnNumber)
        } else {
            return String.class
        }
   }
}
