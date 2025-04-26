package com.spw.rr.utilities

import javax.swing.JTable
import javax.swing.table.JTableHeader
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
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class FrameHelper extends WindowAdapter implements ComponentListener {
    private static final Logger log = LoggerFactory.getLogger(FrameHelper.class)
    private static final PropertySaver saver = PropertySaver.getInstance()
    public static final String X_NAME = "X"
    public static final String Y_NAME = "Y"
    public static final String WIDTH_NAME = "Width"
    public static final String HEIGHT_NAME = "Height"
    public static final String COL_ORDER_NAME = "Col_order" // will be suffixed with column number
    public static final String COL_WIDTH_NAME = "Col_width" // also appended with column number

    private JTable theTable

    void setTable(JTable table) {
        theTable = table
    }

    public static closeAction(Component window, JTable theTable) {
        TableColumnModel columnModel = theTable.getColumnModel()
        for (i in 0..<columnModel.getColumnCount()) {
            TableColumn thisColumn = columnModel.getColumn(i)
            saver.putInt(window.getName(), COL_ORDER_NAME + i.toString(), thisColumn.getModelIndex())
            saver.putInt(window.getName(), COL_WIDTH_NAME + i.toString(), thisColumn.getWidth())
        }
    }


    @Override
    void windowClosing(WindowEvent e) {
        log.debug("Closing the window ${e.getComponent().getName()}")
        if (theTable != null) {
            closeAction(e.getComponent(), theTable)
        } else {
            log.error("theTable is null in FrameHelper.windowClosing")
        }
        saveColumns(e.getComponent().getName(), theTable)
        if (e.getComponent().getName().equals("main")) {
            log.debug("Window closing - saving values")
            saver.writeValues()
        }
        super.windowClosing()
    }

    void saveColumns(String windowName, JTable theTable) {
        TableColumnModel columnModel = theTable.getColumnModel()
        for (i in 0..<columnModel.getColumnCount()) {
            TableColumn thisColumn = columnModel.getColumn(i)
            saver.putInt(windowName, COL_ORDER_NAME + i.toString(), thisColumn.getModelIndex())
            saver.putInt(windowName, COL_WIDTH_NAME + i.toString(), thisColumn.getWidth())
        }
    }

    public static restoreColumns(String name, JTable theTable) {
        JTableHeader header = theTable.getTableHeader()
        theTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF)
        TableColumnModel headerModel = header.getColumnModel()
        TableColumnModel tcModel = theTable.getColumnModel()
        Integer[] desiredColumn = new Integer[tcModel.getColumnCount()]
        boolean widthValid = true
        int[] viewOrder = new int[tcModel.getColumnCount()]
        int[] modelOrder = new int[tcModel.getColumnCount()]
        boolean orderValid = true
        for (i in 0..<tcModel.getColumnCount()) {
            TableColumn thisColumn = tcModel.getColumn(i)
            Integer newValue = saver.getInt(name, COL_ORDER_NAME + i.toString())
            if (newValue != null) {
                desiredColumn[i] = newValue
            } else {
                orderValid = false
            }
            newValue = saver.getInt(name, COL_WIDTH_NAME + i.toString())
            if (newValue != null) {
                thisColumn.setPreferredWidth(newValue)
            } else {
                widthValid = false
            }
        }
        if (orderValid) {
            log.trace("desiredArray is ${desiredColumn}")
            ArrayList<Integer> resultArray = new ArrayList(tcModel.getColumnCount())
            ArrayList<Integer> tempArray = new ArrayList(tcModel.getColumnCount())
            ArrayList<Integer> currentArray = new ArrayList(tcModel.getColumnCount())
            for (i in 0..<tcModel.getColumnCount()) {
                currentArray.add(Integer.valueOf( i))
            }
            for (i in 0..<desiredColumn.size() - 1) {
                if (desiredColumn[i].equals(currentArray.get(i))) {
                    resultArray.add(currentArray.get(i))
                } else {
                    for (j in i+1..<currentArray.size()) {
                        if (desiredColumn[i].equals(currentArray.get(j))) {
                            tcModel.moveColumn(j, i)
                            resultArray.add(currentArray.get(j))
                            log.debug("column ${j} moved to column ${i}")
                            currentArray.remove(j)
                            tempArray.clear()
                            tempArray.addAll(resultArray)
                            for (k in i..<currentArray.size()) {
                                tempArray.add(currentArray.get(k))
                            }
                            log.trace("tempArray is now ${tempArray}")
                            currentArray.clear()
                            currentArray.addAll(tempArray)
                        }
                    }
                }
                log.trace("after moves currentArray is ${currentArray}")
            }
        }
        //theTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS)
    }

    /**
     * get the values from saver and and set location and size
     * @param window the window to apply values to
     * @param name the name used for reference from saver
     * @param width default value for width
     * @param height default for heigth
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
        saver.putInt(name, WIDTH_NAME, (int) dim.getWidth())
        saver.putInt(name, HEIGHT_NAME, (int) dim.getHeight())
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
