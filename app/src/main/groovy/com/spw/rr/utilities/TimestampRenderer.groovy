package com.spw.rr.utilities

import javax.swing.table.DefaultTableCellRenderer

class TimestampRenderer extends DefaultTableCellRenderer {

    public void setValue( Object value) {
        setText((value == null) ? "" : value.toString())
    }
}
