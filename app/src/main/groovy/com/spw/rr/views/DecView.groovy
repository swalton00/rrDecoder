package com.spw.rr.views

import com.spw.rr.controllers.DecController
import com.spw.rr.models.DecModel
import com.spw.rr.utilities.FrameHelper
import com.spw.rr.utilities.ListenerForTables
import com.spw.rr.utilities.PropertySaver
import com.spw.rr.utilities.RrTableModel
import net.miginfocom.swing.MigLayout

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent

class DecView {

    Component parent
    DecController controller
    DecModel model
    static final String D_NAME = "decoders"
    static final String D_CVLIST = "cvList"
    PropertySaver saver = PropertySaver.getInstance()

    RrTableModel tableModel
    ArrayList<JMenuItem> itemList = new ArrayList<>()

    DecView(Component parent, DecController controller, DecModel model) {
        this.parent = parent
        this.controller = controller
        this.model = model
        model.view = this
    }

    void init() {
        model.thisDialog = (Component) (new JDialog(parent, "Decoder View", true))
        model.thisDialog.setName(D_NAME)
        model.thisDialog.addComponentListener(new FrameHelper())
        JDialog tempDialog = (JDialog) (model.thisDialog)
        tempDialog.setLayout(new BorderLayout())
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
        viewMenu.add(model.viewSpeedGraphItem)
        model.viewDecDetailItem = new JMenuItem("View Decoder Details")
        model.viewDecDetailItem.setEnabled(false)
        model.viewDecDetailItem.addActionListener(controller.viewDecDetailAction)
        viewMenu.add(model.viewDecDetailItem)
        model.viewFunctionItem = new JMenuItem("View Function Labels")
        model.viewFunctionItem.setEnabled(false)
        viewMenu.add(model.viewFunctionItem)
        model.viewFunctionItem.addActionListener(controller.viewFunctionAction)
        model.viewKeyPairsItem = new JMenuItem("View Key Value Pairs")
        model.viewKeyPairsItem.setEnabled(false)
        viewMenu.add(model.viewKeyPairsItem)
        model.viewKeyPairsItem.addActionListener(controller.viewSKeyPairsAction)
        model.viewStandCvItem = new JMenuItem("View Standard CV Contents")
        model.viewStandCvItem.setEnabled(false)
        viewMenu.add(model.viewStandCvItem)
        model.viewStandCvItem.addActionListener(controller.viewStandCvAction)
        model.viewSelCvItem = new JMenuItem("View Selected CV Contents")
        model.viewSelCvItem.setEnabled(false)
        viewMenu.add(model.viewSelCvItem)
        model.viewSelCvItem.addActionListener(controller.viewSelCvAction)
        model.viewAllCvItem = new JMenuItem("View All CV Contents")
        model.viewAllCvItem.setEnabled(false)
        viewMenu.add(model.viewAllCvItem)
        model.viewAllCvItem.addActionListener(controller.viewAllCvAction)
        menuBar.add(viewMenu)
        JMenu helpMenu = new JMenu("Help")
        JMenuItem helpMenuItem = new JMenuItem("Help")
        helpMenu.add(helpMenuItem)
        menuBar.add(helpMenu)
        tempDialog.setJMenuBar(menuBar)
        JMenuItem[] addList = [model.viewSpeedProfileItem, model.viewSpeedGraphItem,
                               model.viewDecDetailItem, model.viewFunctionItem,
                               model.viewKeyPairsItem, model.viewStandCvItem,
                               model.viewAllCvItem, model.importDetailItem]
        addList.each {
            itemList.add(it)
        }
        tableModel = new RrTableModel(model)
        model.theTable = new JTable(tableModel)
        model.theTable.setCellSelectionEnabled(false)
        model.theTable.setColumnSelectionAllowed(false)
        model.theTable.setRowSelectionAllowed(true)
        model.theTable.getSelectionModel().addListSelectionListener(new ListenerForTables(model))
        model.tableIsSelected.addPropertyChangeListener {
            itemList.each { item ->
                item.setEnabled((boolean) it.newValue)
            }
            if (it.newValue & model.testCvList()) {
                model.viewSelCvItem.setEnabled(true)
            }
        }
        model.theTable.setAutoCreateRowSorter(true)
        model.theTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        JScrollPane scrollPane = new JScrollPane(model.theTable)
        tempDialog.add(scrollPane, BorderLayout.CENTER)
        JPanel cvPanel = new JPanel(new MigLayout("fill"))
        JLabel cvLabel = new JLabel("CVs: ID, ")
        cvPanel.add(cvLabel, "h 30px:30px:30px, right")
        model.cvListField = new JTextField("")
        model.cvListField.setColumns(40)
        model.cvListField.setName("cvlist")
        model.cvListField.addFocusListener(model)
        String cvList = saver.getField(D_NAME, "cvList")
        if (cvList != null) {
            model.cvListField.setText(cvList)
        }
        cvPanel.add(model.cvListField, "h 30px:30px:30px, left, growx, shrink 0")
        tempDialog.add(cvPanel, BorderLayout.SOUTH)
        FrameHelper.setFrameValues(model.thisDialog, D_NAME, 1200, 900 )
        model.thisDialog.setVisible(true)
    }
}
