package com.spw.rr.views

import com.spw.rr.controllers.MainController
import com.spw.rr.models.MainModel
import com.spw.rr.utilities.FrameHelper
import net.miginfocom.swing.MigLayout

import javax.swing.Action
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JScrollBar
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.KeyStroke
import javax.swing.WindowConstants
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.security.Key

class MainView {

    MainModel model
    MainController controller

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
        model.baseFrame.getContentPane().setLayout(new MigLayout())
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
        model.dataList = new Vector()
        model.theTable = new JTable(10, 3)
        JScrollPane scrollBar = new JScrollPane(model.theTable)
        model.baseFrame.getContentPane().add(scrollBar)
        model.baseFrame.pack()
        model.baseFrame.setVisible(true)

    }
}
