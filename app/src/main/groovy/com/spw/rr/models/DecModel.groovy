package com.spw.rr.models

import com.spw.rr.database.DecoderEntry
import com.spw.rr.utilities.ObservableBean
import com.spw.rr.views.DecView
import groovy.transform.ToString

import javax.swing.JMenuItem
import javax.swing.JTextField
import java.awt.Component
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.util.regex.Matcher
import java.util.regex.Pattern

@ToString(includePackage = false, includeNames = true, includeFields = true)
class DecModel extends RrBaseModel implements  FocusListener {

    {
        columnNames.addAll(["Id" ,
                            "RosterId",
                            "Address",
                            "Speeds?",
                            "Details?",
                            "XML File:",
                            "Road Name",
                            "Road Number",
                            "Manufacturer",
                            "Decoder Family",
                            "Decoder Model",
                            "Owner",
                            "Updated",
                            "Imported",
                            "Details Read"])
        preferredWidths.addAll([10, 10, 10, 5, 5, 30, 20, 10, 10, 15, 20])
    }

    boolean selectedViewImportGood = false
    boolean goodSelection = false

    ArrayList<DecoderEntry> fullList = new ArrayList<>()
    Component thisDialog
    ObservableBean enableCVdetail = new ObservableBean()
    DecView view
    JMenuItem importDetailItem
    JMenuItem filePrintItem

    JMenuItem viewSpeedProfileItem
    JMenuItem viewSpeedGraphItem
    JMenuItem viewDecDetailItem
    JMenuItem viewFunctionItem
    JMenuItem viewKeyPairsItem
    JMenuItem viewSelCvItem
    JMenuItem viewStandCvItem
    JMenuItem ViewAllCvItem

    JTextField cvListField


    void init() {

    }

    @Override
    void focusGained(FocusEvent e) {

    }

    boolean testCvList() {
        if (!cvListField.getText().isBlank()) {
            return true
        } else {
            return false
        }
    }

    @Override
    void focusLost(FocusEvent e) {
        if (e.getComponent().getName().equals("cvlist")) {
            if (goodSelection) {
                viewSelCvItem.setEnabled(false)
                boolean goodValue = testCvList()
                if (goodValue) {
                    viewSelCvItem.setEnabled(true)
                    view.saver.putField(DecView.D_NAME, DecView.D_CVLIST, cvListField.getText())
                }
            }
        }
    }
}
