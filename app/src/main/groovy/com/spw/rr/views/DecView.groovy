package com.spw.rr.views

import com.spw.rr.controllers.DecController
import com.spw.rr.models.DecModel
import com.spw.rr.utilities.FrameHelper
import com.spw.rr.utilities.ListenerForTables
import com.spw.rr.utilities.PropertySaver
import com.spw.rr.utilities.RrTableModel
import com.spw.rr.utilities.TimestampRenderer
import groovy.util.logging.Slf4j
import net.miginfocom.swing.MigLayout

import javax.swing.*
import javax.swing.table.TableRowSorter
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.sql.Timestamp
import java.util.regex.Matcher
import java.util.regex.Pattern

@Slf4j
class DecView {

    Component parent
    DecController controller
    DecModel model
    static final String D_NAME = "decoders"
    static final String D_CVLIST = "cvList"
    static final String D_COLUMN_VISIBLE = "columnVisible"
    PropertySaver saver = PropertySaver.getInstance()
    static final Pattern REGEX_NUMBER = Pattern.compile("^[\\d]+\$")

    RrTableModel tableModel
    ArrayList<Component> itemList = new ArrayList<>()

    DecView(Component parent, DecController controller, DecModel model) {
        this.parent = parent
        this.controller = controller
        this.model = model
        model.view = this
    }

    def roadNumberComparator = { String left, String right ->
        String leftValue = left == null ? "" : left
        String rightValue = right == null ? "" : right
        Matcher leftMatcher = REGEX_NUMBER.matcher(leftValue)
        Matcher rightMatcher = REGEX_NUMBER.matcher(rightValue)
        boolean leftGood = leftMatcher.matches()
        boolean rightGood = rightMatcher.matches()
        if (leftGood & rightGood) {
            Integer leftInt = Integer.valueOf(leftValue)
            Integer rightInt = Integer.valueOf(rightValue)
            if (leftInt < rightInt) {
                return -1
            }
            if (leftInt > rightInt) {
                return 1
            }
            if (leftInt == rightInt) {
                return 0
            }
        }
        if (leftGood & (!rightGood)) {
            return -1
        }
        if ((!leftGood) & rightGood) {
            return 1
        }
        if (leftValue < rightValue) {
            return -1
        }
        if (leftValue > rightValue) {
            return 1
        }
        return 0
    }

    void init() {
        model.thisDialog = (Component) (new JDialog(parent, "Decoder View", true))
        model.thisDialog.setName(D_NAME)
        FrameHelper frameHelper = new FrameHelper()
        model.thisDialog.addComponentListener(frameHelper)
        (JDialog)(model.thisDialog).addWindowListener(frameHelper)
        (JDialog)(model.thisDialog).addWindowListener(new WindowAdapter() {
            @Override
            void windowClosing(WindowEvent e) {
                controller.saveColumnVisibility()
            }
        })
        JDialog tempDialog = (JDialog) (model.thisDialog)
        tempDialog.setLayout(new BorderLayout())
        JMenuBar menuBar = new JMenuBar()
        JMenu fileMenu = new JMenu("File")
        model.importDetailItem = new JMenuItem("Import Detail")
        model.importDetailItem.setEnabled(false)
        model.importDetailItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK))
        model.importDetailItem.addActionListener(controller.importDetailAction)
        fileMenu.add(model.importDetailItem)
        model.filePrintItem = new JMenuItem("Print")
        model.filePrintItem.addActionListener(controller.filePrintAction)
        model.filePrintItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK))
        fileMenu.add(model.filePrintItem)
        model.restoreColumnDefaultsItem = new JMenuItem("Restore Column Defaults")
        model.restoreColumnDefaultsItem.addActionListener(controller.restoreColumnDefaultsAction)
        fileMenu.add(model.restoreColumnDefaultsItem)
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

        model.columnMenu = new JMenu("Show/Hide Columns")
        model.columnNames.eachWithIndex { String columnName, int columnIndex ->
            JCheckBoxMenuItem columnItem = new JCheckBoxMenuItem(columnName)
            String savedVisibility = saver.getField(D_NAME, D_COLUMN_VISIBLE + columnIndex)
            columnItem.setSelected(savedVisibility == null || Boolean.parseBoolean(savedVisibility))
            columnItem.setActionCommand(columnIndex.toString())
            columnItem.addActionListener(controller.columnVisibilityAction)
            model.columnItems.add(columnItem)
            model.columnMenu.add(columnItem)
        }
        viewMenu.add(model.columnMenu)

        model.changeMenu = new JMenu("View Changes")

        JMenu changeAll = new JMenu("All Items")

        model.changeMenu.add(changeAll)
        model.viewAllChangesCVs = new JMenuItem("All CVS - changes")
        model.viewAllChangesCVs.addActionListener(controller.viewChangedAllCVs)
        changeAll.add(model.viewAllChangesCVs)
        model.viewAllChangeLabels = new JMenuItem("All Function Labels")
        model.viewAllChangeLabels.addActionListener(controller.viewChangedAllLabels)
        changeAll.add(model.viewAllChangeLabels)
        model.viewAllChangeKeys = new JMenuItem("All Key Values")
        model.viewAllChangeKeys.addActionListener(controller.viewChangedAllKeys)
        changeAll.add(model.viewAllChangeKeys)

        JMenu onlyChanged = new JMenu("Only Changed")
        model.viewDiffAllCvs = new JMenuItem("Only Changed CVs")
        model.viewDiffAllCvs.addActionListener(controller.viewChangedCVs)
        onlyChanged.add(model.viewDiffAllCvs)
        model.viewDiffLabels = new JMenuItem("Changed Function Labels")
        model.viewDiffLabels.addActionListener(controller.viewDiffLabels)
        onlyChanged.add(model.viewDiffLabels)
        model.viewDiffKeys = new JMenuItem("Changed Key Values")
        model.viewDiffKeys.addActionListener(controller.viewDiffKeys)
        onlyChanged.add(model.viewDiffKeys)
        model.changeMenu.setEnabled(false)
        model.changeMenu.add(onlyChanged)
        viewMenu.add(model.changeMenu)

        menuBar.add(viewMenu)
        JMenu helpMenu = new JMenu("Help")
        JMenuItem helpMenuItem = new JMenuItem("Help")
        helpMenu.add(helpMenuItem)
        menuBar.add(helpMenu)
        tempDialog.setJMenuBar(menuBar)
        JMenuItem[] addList = [model.viewSpeedProfileItem, model.viewSpeedGraphItem,
                               model.viewFunctionItem,
                               model.viewKeyPairsItem, model.viewStandCvItem,
                               model.viewAllCvItem]
        itemList.add(model.changeMenu)
        addList.each {
            itemList.add(it)
        }
        tableModel = new RrTableModel(model)
        model.theTable = new JTable(tableModel)
        frameHelper.setTable(model.theTable)
        model.theTable.setCellSelectionEnabled(false)
        model.theTable.setColumnSelectionAllowed(false)
        model.theTable.setRowSelectionAllowed(true)
        model.theTable.getSelectionModel().addListSelectionListener(new ListenerForTables(model))
        model.tableIsSelected.addPropertyChangeListener {
            itemList.each { item ->
                item.setEnabled((boolean) it.newValue)
            }
            model.goodSelection = it.newValue
            model.importDetailItem.setEnabled(model.selectedViewImportGood & (it.newValue as Boolean))
            if (it.newValue & model.testCvList()) {
                model.viewSelCvItem.setEnabled(true)
            }
        }
        model.theTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        JScrollPane scrollPane = new JScrollPane(model.theTable)
        tempDialog.add(scrollPane, BorderLayout.CENTER)
        JPanel cvPanel = new JPanel(new MigLayout("fill"))
        JLabel cvLabel = new JLabel("CVs: ")
        model.cvListField = new JTextField("")
        model.cvListField.setColumns(40)
        model.cvListField.setName("cvlist")
        ArrayList<Class> classList = new ArrayList()
        classList.add(0, Integer.class)  // id
        classList.add(1, Integer.class)  //rosterid
        classList.add(2, Integer.class)  // dcc address
        classList.add(3, String.class)  //has speed profile
        classList.add(4, String.class)  //has detail
        classList.add(5, String.class)  //file name
        classList.add(6, String.class)  //road name
        classList.add(7, String.class)  //road number
        classList.add(8, Integer.class)  //manufacturer
        classList.add(9, String.class)  //decoder Family
        classList.add(10, String.class)  //decoder model
        classList.add(11, String.class)  //owner
        classList.add(12, Timestamp.class)  //Date updated
        classList.add(13, Timestamp.class)  //import date
        classList.add(14, Timestamp.class)  //detail time
        classList.add(15, Integer.class) // CV version count
        classList.add(16, Integer.class) // Key Values version count
        classList.add(17, Integer.class) // Function label version count
        model.theTable.setDefaultRenderer(Timestamp.class, new TimestampRenderer())
        tableModel.tableClasses = classList
        log.debug("setting tableClasses to ${classList}")
        model.sorter = new TableRowSorter(tableModel)
        model.sorter.setComparator(7, roadNumberComparator as Comparator)
        model.theTable.setRowSorter(model.sorter)
        model.cvListField.addFocusListener(model)
        String cvList = saver.getField(D_NAME, "cvList")
        if (cvList != null) {
            model.cvListField.setText(cvList)
        }
        model.columnItems.eachWithIndex { JCheckBoxMenuItem columnItem, int columnIndex ->
            if (!columnItem.isSelected()) {
                controller.setColumnVisible(columnIndex, false)
            }
        }
        FrameHelper.restoreColumns(D_NAME, model.theTable)
        model.sortButton = new JButton("Sort")
        model.sortButton.setEnabled(false)
        model.sortButton.addActionListener(controller.sortAction)
        cvPanel.add(model.sortButton, "h 30px:30px:30px, left")
        4.times {
            JComboBox<String> sortBox = new JComboBox<String>()
            sortBox.setPrototypeDisplayValue("Decoder Family")
            sortBox.setSelectedIndex(-1)
            sortBox.setEnabled(it == 0)
            sortBox.addActionListener(controller.sortColumnAction)
            model.sortBoxes.add(sortBox)
            cvPanel.add(sortBox, "h 30px:30px:30px, w 140px!, left")
        }
        controller.refreshSortBoxes(-1)
        cvPanel.add(cvLabel, "h 30px:30px:30px, right")
        cvPanel.add(model.cvListField, "h 30px:30px:30px, left, growx, shrink 0")
        tempDialog.add(cvPanel, BorderLayout.SOUTH)
        FrameHelper.setFrameValues(model.thisDialog, D_NAME, 1200, 900 )


        model.thisDialog.setVisible(true)
    }
}
