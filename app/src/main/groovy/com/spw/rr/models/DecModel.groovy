package com.spw.rr.models

import com.spw.rr.database.DecoderEntry
import com.spw.rr.utilities.ObservableBean
import groovy.transform.ToString
import org.apache.tools.ant.taskdefs.modules.Jmod

import javax.swing.JMenuItem
import java.awt.Component

@ToString(includePackage = false, includeNames = true, includeFields = true)
class DecModel extends RrBaseModel{

    {
        columnNames.addAll(["Id" , "RosterId",
                            "Address",
                            "Speeds?",
                            "Details?",
                            "XML File:",
                            "Road Name",
                            "Road Number",
                            "Manufacturer",
                            "Owner",
                            "Updated"])
        preferredWidths.addAll([10, 10, 10, 5, 5, 30, 20, 10, 10, 15, 20])
    }

    ArrayList<DecoderEntry> fullList = new ArrayList<>()
    Component thisDialog
    ObservableBean enableCVdetail = new ObservableBean()

    JMenuItem importDetailItem

    JMenuItem viewSpeedProfileItem
    JMenuItem viewSpeedGraphItem
    JMenuItem viewDecDetailItem
    JMenuItem viewFunctionItem
    JMenuItem viewKeyPairsItem
    JMenuItem viewSelCvItem
    JMenuItem viewStandCvItem
    JMenuItem ViewAllCvItem


    void init() {

    }


}
