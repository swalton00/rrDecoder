package com.spw.rr.views

import com.spw.rr.controllers.DataController
import com.spw.rr.models.DataModel
import com.spw.rr.utilities.FrameHelper
import com.spw.rr.utilities.MultiLineTableHeaderRenderer
import com.spw.rr.utilities.PropertySaver
import com.spw.rr.utilities.RightTableCellRenderer
import com.spw.rr.utilities.RrTableModel
import net.miginfocom.swing.MigLayout
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableColumn
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent

class DataView {

    private static final int D_WIDTH = 1200
    private static final int D_HEIGHT = 900
    private static final Logger log = LoggerFactory.getLogger(DataView.class)
    PropertySaver saver = PropertySaver.getInstance()
    DataController controller
    DataModel model
    JDialog parent
    String dialogTitle
    String viewName

    RrTableModel tableModel

    DataView(JDialog parent,
             DataController controller,
             DataModel model,
             String dialogTitle,
                String viewName) {
        this.parent = parent
        this.model = model
        this.controller = controller
        model.view = this
        this.dialogTitle = dialogTitle
        this.viewName = viewName
        log.trace("dialogTitle is ${dialogTitle} and view name is ${viewName}")
    }

    void init() {
        log.debug("initializing the new view")
        model.dialog = new JDialog(parent, dialogTitle, true)
        model.dialog.setName(viewName)
        model.dialog.addComponentListener(new FrameHelper())
        model.dialog.getContentPane().setLayout(new MigLayout("fill"))
        JMenuBar menuBar = new JMenuBar()
        JMenu fileMenu = new JMenu("File")
        menuBar.add(fileMenu)
        JMenu helpMenu = new JMenu("Help")
        JMenuItem fileCloseItem = new JMenuItem("Close")
        fileCloseItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK))
        fileCloseItem.addActionListener(controller.closeAction)
        fileMenu.add(fileCloseItem)
        menuBar.add(helpMenu)
        JMenuItem helpItem = new JMenuItem("Help")
        helpItem.addActionListener(controller.helpActiom)
        helpMenu.add(helpItem)
        model.dialog.getContentPane().add(menuBar, "wrap")
        tableModel = new RrTableModel(model)
        tableModel = new RrTableModel(model)
        model.theTable = new JTable(tableModel)
        model.theTable.setCellSelectionEnabled(false)
        model.theTable.setColumnSelectionAllowed(false)
        model.theTable.setRowSelectionAllowed(false)
        MultiLineTableHeaderRenderer renderer = new MultiLineTableHeaderRenderer()
        Enumeration enumA = model.theTable.getColumnModel().getColumns()
        while (enumA.hasMoreElements()) {
            ((TableColumn) enumA.nextElement()).setHeaderRenderer(renderer)
        }
        DefaultTableCellRenderer rightRenderer = new RightTableCellRenderer()
        MultiLineTableHeaderRenderer headerRenderer = new MultiLineTableHeaderRenderer()
        for (i in 1..<model.columnNames.size()) {
            model.theTable.getColumnModel().getColumn(i).setCellRenderer(rightRenderer)
            model.theTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer)
        }
        JScrollPane scrollPane = new JScrollPane(model.theTable)
        model.dialog.getContentPane().add(scrollPane, "grow")
        Integer dWidth = saver.getInt(viewName, FrameHelper.WIDTH_NAME)
        Integer dHeight = saver.getInt(viewName, FrameHelper.HEIGHT_NAME)
        if (dWidth == null) {
            dWidth = D_WIDTH
            saver.putInt(viewName, FrameHelper.WIDTH_NAME, dWidth)
        }
        if (dHeight == null) {
            dHeight = D_HEIGHT
            saver.putInt(viewName, FrameHelper.HEIGHT_NAME, dHeight)
        }
        model.dialog.setSize(dWidth, dHeight)
        Integer xPos = saver.getInt(viewName, FrameHelper.X_NAME)
        Integer yPos = saver.getInt(viewName, FrameHelper.Y_NAME)
        if (xPos == null | yPos == null) {
            Toolkit toolkit = Toolkit.getDefaultToolkit()
            Dimension screen =toolkit.getScreenSize()
            xPos = (screen.width - dWidth) / 2
            yPos = (screen.height - dHeight) / 2
        }
        model.dialog.setLocation(xPos, yPos)
        model.dialog.setVisible(true)
    }

}
