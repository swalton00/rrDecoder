package com.spw.rr.utilities

import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.JTextArea
import javax.swing.LookAndFeel
import javax.swing.SwingConstants
import javax.swing.table.TableCellRenderer
import java.awt.Component

public class MultiLineTableHeaderRenderer extends JTextArea implements TableCellRenderer {

    public MultiLineTableHeaderRenderer() {
        setEditable(false);
        setLineWrap(true);
        setOpaque(false);
        setFocusable(false);
        setWrapStyleWord(true);
        LookAndFeel.installBorder(this, "TableHeader.cellBorder");
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof JLabel) {
            ((JLabel) value).setHorizontalAlignment(SwingConstants.CENTER);
        }
        int width = table.getColumnModel().getColumn(column).getWidth();
        setText((String)value);
        setSize(width, (Integer)    getPreferredSize().height);
        return this;
    }
}