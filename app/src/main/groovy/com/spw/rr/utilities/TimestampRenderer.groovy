package com.spw.rr.utilities

import sun.swing.DefaultLookup

import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.LookAndFeel
import javax.swing.UIDefaults
import javax.swing.UIManager
import javax.swing.table.TableCellRenderer
import java.awt.Color
import java.awt.Component
import java.sql.Timestamp

class TimestampRenderer extends JLabel implements TableCellRenderer{
    @Override
    Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value.class != Timestamp.class) {
            throw new RuntimeException("Error - renderer called for the wrong class")
        }
        setFont(table.getFont())
        setText(value.toString())
        LookAndFeel ui = UIManager.getLookAndFeel()
        UIDefaults defaults = ui.getDefaults()


        Color col;
        col = defaults.getColor( "Table.focusCellForeground");
        if (col != null) {
            super.setForeground(col);
        }
        col = defaults.getColor("Table.focusCellBackground");
        if (col != null) {
            super.setBackground(col);
        }
        return this
    }
}
