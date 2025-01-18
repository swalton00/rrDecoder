package com.spw.rr.views

import com.spw.rr.controllers.MainController
import com.spw.rr.models.MainModel
import com.spw.rr.utilities.FrameHelper
import com.spw.rr.utilities.ListenerForTables
import com.spw.rr.utilities.PropertySaver
import com.spw.rr.utilities.RrTableModel
import net.miginfocom.swing.MigLayout

import javax.swing.Action
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.KeyStroke
import javax.swing.ListSelectionModel
import javax.swing.WindowConstants
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.lang.annotation.Target


class MainView {

    MainModel model
    MainController controller
    PropertySaver saver = PropertySaver.getInstance()

    RrTableModel tableModel

    MainView() {

    }

    MainView(MainController controller, MainModel model) {
        this.model = model
        this.controller = controller
    }

    void init() {
        model.baseFrame = new JFrame("Model Railroad Decoder Database App")
        model.baseFrame.setName("main")
        model.baseFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
        model.baseFrame.addComponentListener(new FrameHelper())
        model.baseFrame.getContentPane().setLayout(new MigLayout("fill"))
        JMenuBar menuBar = new JMenuBar()

        JMenu fileMenu = new JMenu("File")
        fileMenu.setMnemonic(KeyEvent.VK_F)
        menuBar.add(fileMenu)
        JMenuItem fileImportMenu = new JMenuItem("Import")
        fileImportMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK))
        fileImportMenu.addActionListener(controller.importAction)
        fileMenu.add(fileImportMenu)
        JMenuItem closeMenuItem = new JMenuItem("Close")
        closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK))
        closeMenuItem.addActionListener(controller.closeAtion)
        fileMenu.add(closeMenuItem)

        JMenu settingsMenu = new JMenu("Settings")
        menuBar.add(settingsMenu)
        JMenuItem settingEditItem = new JMenuItem("Edit")
        settingEditItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK))
        settingEditItem.addActionListener(controller.settingsAction)
        settingsMenu.add(settingEditItem)

        JMenu viewMenu = new JMenu("View")
        menuBar.add(viewMenu)
        JMenuItem viewAllItem = new JMenuItem("List ALL Decoders")
        viewMenu.add(viewAllItem)
        model.viewItem = new JMenuItem("View Selected Decoders")
        model.viewItem.setEnabled(false)
        model.viewItem.addActionListener(controller.viewAction)
        viewMenu.add(model.viewItem)

        JMenu helpMenu = new JMenu("Help")
        helpMenu.setMnemonic(KeyEvent.VK_H)
        JMenuItem showHelpItem = new JMenuItem("Help")
        showHelpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.ALT_MASK))
        showHelpItem.addActionListener (controller.helpAction)
        helpMenu.add(showHelpItem)
        JMenuItem aboutItem = new JMenuItem("About")
        aboutItem.addActionListener(controller.aboutAction)
        helpMenu.add(aboutItem)
        menuBar.add(helpMenu)

        model.baseFrame.getContentPane().add(menuBar, "wrap")
        tableModel = new RrTableModel(model)
        model.theTable = new JTable(tableModel)
        model.theTable.setCellSelectionEnabled(false)
        model.theTable.setColumnSelectionAllowed(false)
        model.theTable.setRowSelectionAllowed(true)
        model.theTable.getSelectionModel().addListSelectionListener(new ListenerForTables(model))
        model.theTable.setAutoCreateRowSorter(true)
        model.theTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        model.theTable.setPreferredSize(new Dimension(1000, 2000))
        model.theTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF)

        JScrollPane scrollPane = new JScrollPane(model.theTable)
        scrollPane.setPreferredSize(new Dimension(1500, 1200))
        model.baseFrame.getContentPane().add(scrollPane, "grow")

        Dimension frameSize = model.baseFrame.getSize()
        Integer frameWidth = saver.getInt("main", FrameHelper.WIDTH_NAME)
        Integer frameHeight = saver.getInt("main", FrameHelper.HEIGHT_NAME)
        if (frameWidth == null) {
            frameWidth = 1500
            saver.putInt("main", FrameHelper.WIDTH_NAME)
        }
        if (frameHeight == null) {
            frameHeight = 1200
            saver.putInt("main", FrameHelper.HEIGHT_NAME, frameHeight)
        }
        FrameHelper.setFrameValues(model.baseFrame, "main", frameWidth, frameHeight)
        Integer frameX = saver.getInt("main", FrameHelper.X_NAME)
        Integer frameY = saver.getInt("main", FrameHelper.Y_NAME)
        if (frameX == null | frameY == null) {
            Toolkit toolkit = Toolkit.getDefaultToolkit()
            Dimension dim = toolkit.getScreenSize()
            frameY = (dim.height - frameHeight) / 2
            frameX = (dim.width - frameWidth) / 2
            saver.putInt("main", FrameHelper.X_NAME, frameX)
            saver.putInt("main", FrameHelper.Y_NAME, frameY)
        }
        model.baseFrame.setLocation(frameX, frameY)
        model.baseFrame.pack()
        model.baseFrame.setVisible(true)

    }
}
