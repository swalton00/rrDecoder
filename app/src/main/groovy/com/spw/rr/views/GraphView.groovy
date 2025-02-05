package com.spw.rr.views

import com.spw.rr.controllers.GraphController
import com.spw.rr.models.GraphModel
import com.spw.rr.utilities.FrameHelper
import com.spw.rr.utilities.PropertySaver

import javax.swing.JDialog
import java.awt.Dimension
import java.awt.Toolkit

class GraphView {

    JDialog parent
    GraphModel model
    GraphController controller
    private static final String SAVER_NAME = "graphview"

    PropertySaver saver = PropertySaver.getInstance()

    GraphView(JDialog parent, GraphController controller, GraphModel model) {
        this.parent = parent
        this.controller = controller
        this.model = model
    }

    void init() {
        model.graphDialog = new JDialog(parent, "Graph of Speed Profile", true)
        model.graphDialog.setName(SAVER_NAME)
        model.graphDialog.addComponentListener(new FrameHelper())
        Integer dialogWidth = saver.getInt(SAVER_NAME, FrameHelper.WIDTH_NAME)
        Integer dialogHeight = saver.getInt(SAVER_NAME, FrameHelper.HEIGHT_NAME)
        if (dialogWidth == null) {
            dialogWidth = 1200
            saver.putInt(SAVER_NAME, FrameHelper.WIDTH_NAME, dialogWidth)
        }
        if (dialogHeight == null) {
            dialogHeight = 900
            saver.putInt(SAVER_NAME, FrameHelper.HEIGHT_NAME, dialogHeight)
        }
        Integer dialogX = saver.getInt(SAVER_NAME, FrameHelper.X_NAME)
        Integer dialogY = saver.getInt(SAVER_NAME, FrameHelper.Y_NAME)
        if (dialogX == null | dialogY == null) {
            Toolkit toolkit = Toolkit.getDefaultToolkit()
            Dimension dim = toolkit.getScreenSize()
            dialogX = (dim.width - dialogWidth) / 2
            dialogY = (dim.height - dialogHeight) / 2
            saver.putInt(SAVER_NAME, FrameHelper.X_NAME, dialogX)
            saver.putInt(SAVER_NAME, FrameHelper.Y_NAME, dialogY)
        }
        model.graphDialog.setSize(dialogWidth, dialogHeight)
        model.graphDialog.setLocation(dialogX, dialogY)
        model.graphDialog.setContentPane(model.chartPanel)
        model.graphDialog.setVisible(true)
    }
}
