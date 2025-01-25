package com.spw.rr.utilities

import javax.swing.JLabel
import javax.swing.table.DefaultTableCellRenderer

class RightTableCellRenderer extends DefaultTableCellRenderer {
    RightTableCellRenderer() {
        setHorizontalAlignment(JLabel.RIGHT)
    }
}