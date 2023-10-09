package com.spw.rr

import griffon.transform.Observable

import javax.swing.JTable
import javax.swing.table.TableModel

class RRBaseModel {

    String[] columnNames = ["Id", "System", "Full Path"]

    int[] preferredWidths = [10,20,300]

    JTable theTable

    TableModel tableModel

    @Observable
    boolean tableSelectionEnabled = false

}
