package com.spw.rr.models

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.EventList
import com.spw.rr.utilities.ObservableBean

import javax.swing.JTable
import javax.swing.table.TableModel

class RrBaseModel {


    public ArrayList<String> columnNames = new ArrayList<>()
    public ArrayList<Integer> preferredWidths = new ArrayList<>()
    EventList<ArrayList<String>> tableList = new BasicEventList<ArrayList<String>>()
    JTable theTable
    // the id's of the rows selected (ID's are always the first element in the row
    ArrayList<Integer> selectedRows = new ArrayList<>()
    TableModel tableModel

    ObservableBean  tableIsSelected = new ObservableBean(value: false)

}
