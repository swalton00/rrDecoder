package com.spw.rr

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.EventList
import griffon.transform.Observable

import javax.swing.JTable
import javax.swing.table.TableModel

class RRBaseModel {

    ArrayList<String> columnNames = new ArrayList<>()
    ArrayList<Integer> preferredWidths = new ArrayList<>()
    EventList<ArrayList<String>> tableList = new BasicEventList<ArrayList<String>>()
    JTable theTable
    ArrayList<Integer> selectedRows = new ArrayList<>()
    TableModel tableModel

    @Observable
    boolean tableSelectionEnabled = false

}
