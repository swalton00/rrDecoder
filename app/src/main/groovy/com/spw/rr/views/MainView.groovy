package com.spw.rr.views

import com.spw.rr.controllers.MainController
import com.spw.rr.models.MainModel
import com.spw.rr.utilities.FrameHelper
import com.spw.rr.utilities.ListenerForTables
import com.spw.rr.utilities.PropertySaver
import com.spw.rr.utilities.RrTableModel
import com.spw.rr.utilities.TimestampRenderer
import net.miginfocom.swing.MigLayout
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.KeyStroke
import javax.swing.ListSelectionModel
import javax.swing.WindowConstants
import javax.swing.table.JTableHeader
import javax.swing.table.TableColumn
import javax.swing.table.TableColumnModel
import javax.swing.table.TableModel
import javax.swing.table.TableRowSorter
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.lang.reflect.WildcardType
import java.sql.Timestamp

class MainView {

    MainModel model
    MainController controller
    PropertySaver saver = PropertySaver.getInstance()
    private static final Logger log = LoggerFactory.getLogger(MainView.class)
    private static final String WINDOW_NAME = "main"

    RrTableModel tableModel

    MainView() {

    }

    MainView(MainController controller, MainModel model) {
        this.model = model
        this.controller = controller
    }

    void init() {
        model.baseFrame = new JFrame("Model Railroad Decoder Database App")
        model.baseFrame.setName(WINDOW_NAME)
        model.baseFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
        model.baseFrame.addComponentListener(new FrameHelper())
        model.baseFrame.addWindowListener(new WindowAdapter() {
            @Override
            void windowClosing(WindowEvent e) {
                // save the column order
                FrameHelper.closeAction(model.baseFrame, model.theTable)
                TableColumnModel columnModel = model.theTable.getColumnModel()
                for (i in 0..<columnModel.getColumnCount()) {
                    TableColumn thisColumn = columnModel.getColumn(i)
                    saver.putInt(WINDOW_NAME, FrameHelper.COL_ORDER_NAME + i.toString(), thisColumn.getModelIndex())
                    saver.putInt(WINDOW_NAME, FrameHelper.COL_WIDTH_NAME + i.toString(), thisColumn.getWidth())
                }
                super.windowClosed(e)
                log.debug("Window closing - saving values")
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
        showHelpItem.addActionListener(controller.helpAction)
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
        model.theTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        Integer mainWidth = saver.getInt("main", FrameHelper.WIDTH_NAME)
        Integer mainHeight = saver.getInt("main", FrameHelper.HEIGHT_NAME)
        if (mainWidth == null) {
            mainWidth = 1000
        }
        if (mainHeight == null) {
            mainHeight = 1500
        }
        model.baseFrame.setPreferredSize(new Dimension(mainWidth, mainHeight))
        JScrollPane scrollPane = new JScrollPane(model.theTable)
        model.baseFrame.getContentPane().add(scrollPane, "grow")
        FrameHelper.setFrameValues(model.baseFrame, "main", 1500, 1200)
        model.theTable.setIntercellSpacing(new Dimension(6, 4))
        ArrayList<Class> classList = new ArrayList<>()
        classList.add(0, Integer.class)
        classList.add(1, String.class)
        classList.add(2, Integer.class)
        classList.add(3, String.class)
        classList.add(4, Timestamp.class)
        classList.add(5, Timestamp.class)
        model.theTable.setDefaultRenderer(Timestamp.class, new TimestampRenderer())
        tableModel.tableClasses = classList
        model.theTable.setRowSorter(new TableRowSorter(tableModel))
        TableColumnModel tcModel = model.theTable.getColumnModel()
        JTableHeader header = model.theTable.getTableHeader()
        model.theTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF)
        TableColumnModel headerModel = header.getColumnModel()
        Integer[] desiredColumn = new Integer[tcModel.getColumnCount()]
        boolean widthValid = true
        int[] viewOrder = new int[tcModel.getColumnCount()]
        int[] modelOrder = new int[tcModel.getColumnCount()]
        boolean orderValid = true
        for (i in 0..<tcModel.getColumnCount()) {
            TableColumn thisColumn = tcModel.getColumn(i)
            Integer newValue = saver.getInt(WINDOW_NAME, FrameHelper.COL_ORDER_NAME + i.toString())
            if (newValue != null) {
                desiredColumn[i] = newValue
            } else {
                orderValid = false
            }
            newValue = saver.getInt(WINDOW_NAME, FrameHelper.COL_WIDTH_NAME + i.toString())
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
        model.theTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS)
        model.baseFrame.pack()
        model.baseFrame.setVisible(true)
    }
}
