package com.spw.rr.models

import com.spw.rr.controllers.GraphController
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.data.category.DefaultCategoryDataset

import javax.swing.JDialog

class GraphModel {

    GraphController controller

    GraphModel(GraphController controller) {
        this.controller = controller
    }

    DefaultCategoryDataset dataset
    JFreeChart chart
    JDialog graphDialog
    ChartPanel chartPanel
    void init() {

    }
}
