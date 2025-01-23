package com.spw.rr.models

import com.spw.rr.controllers.SeeProgressController

import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JProgressBar
import javax.swing.SwingConstants

class SeeProgressModel {

    SeeProgressController controller

    JDialog theDialog

    SeeProgressModel(SeeProgressController controller) {
        this.controller = controller
    }

    void init() {

    }

    JProgressBar bar1 = new JProgressBar(SwingConstants.HORIZONTAL)
    JProgressBar bar2 = new JProgressBar(SwingConstants.HORIZONTAL)
    JProgressBar bar3 = new JProgressBar(SwingConstants.HORIZONTAL)

    JLabel label1 = new JLabel("")
    JLabel stepLabel = new JLabel("")
    JLabel label2 = new JLabel("")
    JLabel label3 = new JLabel("")




}
