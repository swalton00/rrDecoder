package com.spw.rr.views

import com.spw.rr.controllers.MainController
import com.spw.rr.models.MainModel
import com.spw.rr.utilities.FrameHelper
import com.spw.rr.utilities.ListenerForTables
import com.spw.rr.utilities.PropertySaver
import com.spw.rr.utilities.RrTableModel
import net.miginfocom.swing.MigLayout
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.KeyStroke
import javax.swing.ListSelectionModel
import javax.swing.WindowConstants
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class MainView {

    MainModel model
    MainController controller
    PropertySaver saver = PropertySaver.getInstance()
    private static final Logger log = LoggerFactory.getLogger(MainView.class)

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
        model.baseFrame.addWindowListener(new WindowAdapter() {
            @Override
            void windowClosing(WindowEvent e) {
                super.windowClosed(e)
                log.debug("Window cloaing - saving values")
                saver.writeValues()
            }
        })
        model.baseFrame.getContentPane().setLayout(new MigLayout("fill"))
        JMenuBar menuBar = new JMenuBar()

        JMenu fileMenu = new JMenu("File")
        fileMenu.setMnemonic(KeyEvent.VK_F)
        menuBar.add(fileMenu)
        JMenuItem fileImportMenu = new JMenuItem("Import")
        fileImportMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK))
        fileImportMenu.addActionListener(controller.importAction)
        fileMenu.add(fileImportMenu)
        model.importDetailItem = new JMenuItem("Import Details")
        model.importDetailItem.addActionListener(controller.importDetailAction)
        model.importDetailItem.setEnabled(false)
        fileMenu.add(model.importDetailItem)
        JMenuItem closeMenuItem = new JMenuItem("Close")
        closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK))
        closeMenuItem.addActionListener(controller.closeAction)
        fileMenu.add(closeMenuItem)

        JMenu settingsMenu = new JMenu("Settings")
        menuBar.add(settingsMenu)
        JMenuItem settingEditItem = new JMenuItem("Edit")
        settingEditItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK))
        settingEditItem.addActionListener(controller.settingsAction)
        settingsMenu.add(settingEditItem)

        JMenu viewMenu = new JMenu("View")
        menuBar.add(viewMenu)
        JMenuItem viewAllItem = new JMenuItem("View ALL Decoders")
        viewAllItem.addActionListener(controller.viewAllAction)
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
        model.tableIsSelected.addPropertyChangeListener {
            if (it.newValue) {
                model.viewItem.setEnabled(true)
                model.importDetailItem.setEnabled(true)
            } else if (!it.newValue) {
                model.viewItem.setEnabled(false)
                model.importDetailItem.setEnabled(false)
            }
        }
        model.theTable.setAutoCreateRowSorter(true)
        model.theTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        model.theTable.setPreferredSize(new Dimension(1000, 2000))
        //model.theTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF)

        JScrollPane scrollPane = new JScrollPane(model.theTable)
        //scrollPane.setPreferredSize(new Dimension(1500, 1200))
        model.baseFrame.getContentPane().add(scrollPane, "grow")

        FrameHelper.setFrameValues(model.baseFrame, "main", 1500, 1200)
        model.baseFrame.pack()
        model.baseFrame.setVisible(true)

    }
}
