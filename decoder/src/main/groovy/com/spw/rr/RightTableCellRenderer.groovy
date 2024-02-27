package com.spw.rr

import javax.swing.JLabel
import javax.swing.table.DefaultTableCellRenderer

class RightTableCellRenderer extends DefaultTableCellRenderer {
    RightTableCellRenderer() {
        setHorizontalAlignment(JLabel.RIGHT)
    }
}
