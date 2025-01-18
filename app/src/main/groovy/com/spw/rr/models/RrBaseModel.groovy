package com.spw.rr.models

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.EventList

import javax.swing.JTable
import javax.swing.table.TableModel

class RrBaseModel {


    public ArrayList<String> columnNames = new ArrayList<>()
    public ArrayList<Integer> preferredWidths = new ArrayList<>()
    EventList<ArrayList<String>> tableList = new BasicEventList<ArrayList<String>>()
    JTable theTable
    ArrayList<Integer> selectedRows = new ArrayList<>()
    TableModel tableModel

    boolean tableIsSelected = false
}
