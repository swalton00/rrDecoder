package com.spw.rr.views

import com.spw.rr.controllers.DecController
import com.spw.rr.models.DecModel
import com.spw.rr.utilities.FrameHelper
import com.spw.rr.utilities.ListenerForTables
import com.spw.rr.utilities.PropertySaver
import com.spw.rr.utilities.RrTableModel
import net.miginfocom.swing.MigLayout

import javax.swing.JDialog
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.KeyStroke
import javax.swing.ListSelectionModel
import java.awt.Component
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent

class DecView {

    Component parent
    DecController controller
    DecModel model
    static final String D_NAME = "decoders"
    PropertySaver saver = PropertySaver.getInstance()

    RrTableModel tableModel
    JMenuItem[] itemList = [model.viewSpeedProfileItem, model.viewSpeedGraphItem,
            model.viewDecDetailItem, model.viewFunctionItem,
            model.viewKeyPairsItem, model.viewStandCvItem, model.viewSelCvItem]

    DecView(Component parent, DecController controller, DecModel model) {
        this.parent = parent
        this.controller = controller
        this.model = model
    }

    void init() {
        model.thisDialog = (Component) (new JDialog(parent, "Decoder View", true))
        model.thisDialog.setName(D_NAME)
        model.thisDialog.addComponentListener(new FrameHelper())
        JDialog tempDialog =   (JDialog)(model.thisDialog)
        tempDialog.setLayout(new MigLayout("fill"))
        JMenuBar menuBar = new JMenuBar()
        JMenu fileMenu = new JMenu("File")
        model.importDetailItem = new JMenuItem("Import Detail")
        model.importDetailItem.setEnabled(false)
        model.importDetailItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK))
        model.importDetailItem.addActionListener(controller.importDetailAction)
        fileMenu.add(model.importDetailItem)
        JMenuItem fileCloseItem = new JMenuItem("Close")
        fileCloseItem.addActionListener(controller.fileCloseAction)
        fileCloseItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK))
        fileMenu.add(fileCloseItem)
        menuBar.add(fileMenu)
        JMenu viewMenu = new JMenu("View")
        model.viewSpeedProfileItem = new JMenuItem("View Speed Profiles")
        model.viewSpeedProfileItem.setEnabled(false)
        model.viewSpeedProfileItem.addActionListener(controller.viewSpeedProfileAction)
        viewMenu.add(model.viewSpeedProfileItem)
        model.viewSpeedGraphItem = new JMenuItem("Graph Speed Profiles")
        model.viewSpeedGraphItem.setEnabled(false)
        model.viewSpeedGraphItem.addActionListener(controller.viewSpeedGraphAction)
        viewMenu.add(model.viewSpeedProfileItem)
        model.viewSpeedProfileItem.addActionListener(controller.viewSpeedProfileAction)
        model.viewDecDetailItem = new JMenuItem("View Decoder Details")
        model.viewDecDetailItem.setEnabled(false)
        model.viewDecDetailItem.addActionListener(controller.viewDecDetailAction)
        viewMenu.add(model.viewDecDetailItem)
        model.viewFunctionItem = new JMenuItem("View Decoder Details")
        model.viewFunctionItem.setEnabled(false)
        viewMenu.add(model.viewFunctionItem)
        model.viewFunctionItem.addActionListener(controller.viewFunctionAction)
        model.viewKeyPairsItem = new JMenuItem("View Decoder Details")
        model.viewKeyPairsItem.setEnabled(false)
        viewMenu.add(model.viewKeyPairsItem)
        model.viewKeyPairsItem.addActionListener(controller.viewSKeyPairsAction)
        model.viewStandCvItem = new JMenuItem("View Decoder Details")
        model.viewStandCvItem.setEnabled(false)
        viewMenu.add(model.viewStandCvItem)
        model.viewStandCvItem.addActionListener(controller.viewStandCvAction)
        model.viewSelCvItem = new JMenuItem("View Decoder Details")
        model.viewSelCvItem.setEnabled(false)
        viewMenu.add(model.viewSelCvItem)
        model.viewSelCvItem.addActionListener(controller.viewSelCvAction)
        model.viewAllCvItem = new JMenuItem("View Decoder Details")
        model.viewAllCvItem.setEnabled(false)
        viewMenu.add(model.viewAllCvItem)
        model.viewAllCvItem.addActionListener(controller.viewAllCvAction)
        menuBar.add(viewMenu)
        JMenu helpMenu = new JMenu("Help")
        JMenuItem helpMenuItem = new JMenuItem("Help")
        helpMenu.add(helpMenuItem)
        menuBar.add(helpMenu)
        tempDialog.setJMenuBar(menuBar)

        tableModel = new RrTableModel(model)
        model.theTable = new JTable(tableModel)
        model.theTable.setCellSelectionEnabled(false)
        model.theTable.setColumnSelectionAllowed(false)
        model.theTable.setRowSelectionAllowed(true)
        model.theTable.getSelectionModel().addListSelectionListener(new ListenerForTables(model))
        model.tableIsSelected.addPropertyChangeListener {
            itemList.each { item ->
                item.setEnabled((boolean)it.newValue)
            }
        }
        model.theTable.setAutoCreateRowSorter(true)
        model.theTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        JScrollPane scrollPane = new JScrollPane(model.theTable)
        tempDialog.add(scrollPane, "grow")
        Integer dialogWidth = saver.getInt(D_NAME, FrameHelper.WIDTH_NAME)
        Integer dialogHeight = saver.getInt(D_NAME, FrameHelper.HEIGHT_NAME)
        Dimension dialogSize = model.thisDialog.getSize()
        if (dialogWidth == null) {
            dialogWidth = 1200
            saver.putInt(D_NAME, FrameHelper.WIDTH_NAME, dialogWidth)
        }
        if (dialogHeight == null) {
            dialogHeight = 900
            saver.putInt(D_NAME, FrameHelper.HEIGHT_NAME, dialogHeight)
        }
        Integer dialogX = saver.getInt(D_NAME, FrameHelper.X_NAME)
        Integer dialogY = saver.getInt(D_NAME, FrameHelper.Y_NAME)
        if (dialogX == null | dialogY == null) {
            Toolkit toolkit = Toolkit.getDefaultToolkit()
            Dimension dim = toolkit.getScreenSize()
            dialogX = (dim.width - dialogWidth) / 2
            dialogY = (dim.height - dialogHeight) / 2
            saver.putInt(D_NAME, FrameHelper.X_NAME, dialogX)
            saver.putInt(D_NAME, FrameHelper.Y_NAME, dialogY)
        }
        model.thisDialog.setSize(dialogWidth, dialogHeight)
        model.thisDialog.setLocation(dialogX, dialogY)
        model.thisDialog.setVisible(true)

    }
}
