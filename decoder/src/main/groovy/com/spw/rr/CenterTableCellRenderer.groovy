package com.spw.rr

import javax.swing.JLabel
import javax.swing.table.DefaultTableCellRenderer

class CenterTableCellRenderer extends DefaultTableCellRenderer {
    CenterTableCellRenderer() {
        setHorizontalAlignment(JLabel.CENTER)
    }
}
