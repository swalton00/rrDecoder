package com.spw.rr.utilities

import com.sun.javafx.tk.Toolkit

import javax.swing.JTable
import javax.swing.table.TableColumn
import javax.swing.table.TableColumnModel
import java.awt.Component
import java.awt.Dimension
import java.awt.Point
import java.awt.Window
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FrameHelper implements ComponentListener{
    private static final Logger log = LoggerFactory.getLogger(FrameHelper.class)
    private static final PropertySaver saver = PropertySaver.getInstance()
    public static final String X_NAME = "X"
    public static final String Y_NAME = "Y"
    public static final String WIDTH_NAME = "Width"
    public static final String HEIGHT_NAME = "Height"
    public static final String COL_ORDER_NAME = "Col_order" // will be suffixed with column number
    public static final String COL_WIDTH_NAME = "Col_width" // also appended with column number

    public static  closeAction(Component window, JTable theTable) {
        TableColumnModel columnModel = theTable.getColumnModel()
        for (i in 0..<columnModel.getColumnCount()) {
            TableColumn thisColumn = columnModel.getColumn(i)
            saver.putInt(window.getName(), COL_ORDER_NAME + i.toString(), thisColumn.getModelIndex())
            saver.putInt(window.getName(), COL_WIDTH_NAME + i.toString(), thisColumn.getWidth())
        }
    }

    /**
     * get the values from saver and and set location and size
     * @param window    the window to apply values to
     * @param name      the name used for reference from saver
     * @param width     default value for width
     * @param height    default for heigth
     */
    public static void setFrameValues(Window window, String name, int width, int height) {
        Integer wid = saver.getInt(name, WIDTH_NAME)
        Integer high = saver.getInt(name, HEIGHT_NAME)
        boolean sizeFound = true
        if (wid == null) {
            wid = width
            sizeFound = false
        }
        if (high == null) {
            high = height
            sizeFound = false
        }
        window.setSize(new Dimension(wid, high))
        if (!sizeFound) {
            saver.putInt(name, WIDTH_NAME, wid)
            saver.putInt(name, HEIGHT_NAME, high)
        }
        Integer locX = saver.getInt(name, X_NAME)
        Integer locY = saver.getInt(name, Y_NAME)
        if (locX == null | locY == null) {
            java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit()
            int screenWidth = toolkit.getScreenSize().width
            int screenHeight = toolkit.getScreenSize().height
            locX = (screenWidth - wid) / 2
            locY = (screenHeight - high) / 2
            saver.putInt(name, X_NAME, locX)
            saver.putInt(name, Y_NAME, locY)
        }
        window.setLocation(locX, locY)
    }

    @Override
    void componentResized(ComponentEvent e) {
        Component source = e.getComponent()
        String name = source.getName()
        Dimension dim = source.getSize()
        saver.putInt(name, WIDTH_NAME, (int)dim.getWidth())
        saver.putInt(name, HEIGHT_NAME, (int)dim.getHeight())
    }

    @Override
    void componentMoved(ComponentEvent e) {
        Component source = e.getComponent()
        String name = source.getName()
        Point p = e.getComponent().getLocation()
        saver.putInt(name, X_NAME, (int) p.getX())
        saver.putInt(name, Y_NAME, (int) p.getY())
    }

    @Override
    void componentShown(ComponentEvent e) {
    }

    @Override
    void componentHidden(ComponentEvent e) {
    }
}
