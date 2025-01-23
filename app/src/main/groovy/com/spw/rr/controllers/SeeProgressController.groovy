package com.spw.rr.controllers

import com.spw.rr.models.SeeProgressModel
import com.spw.rr.views.SeeProgressView
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.SwingUtilities
import java.awt.Component

class SeeProgressController {

    private static final Logger log = LoggerFactory.getLogger(SeeProgressController.class)
    Component parent
    SeeProgressModel model
    SeeProgressView view

    SeeProgressController(Component parent) {
        log.trace("creating a Progress Controller")
        this.parent = parent
        model = new SeeProgressModel(this)
        model.init()
        view = new SeeProgressView(parent, this, model)
        view.init()
        log.debug("all components for progress now initialized")
    }

    void setMainOverall(int min, int max) {
        log.debug("setting main overall to ${min} and ${max}")
        SwingUtilities.invokeAndWait {
            model.bar1.setMinimum(min)
            model.bar1.setMaximum(max)
            model.bar2.setMinimum(0)
            model.bar2.setMaximum(0)
            model.bar3.setMinimum(0)
            model.bar3.setMaximum(0)
            model.label1.setText("")
            model.label2.setText("")
            model.label3.setText("")
            model.stepLabel.setText("")
            view.show()
        }
    }

    void setMainProgress(int current, String label) {
        log.debug("main Progress at ${current} - ${label}")
        SwingUtilities.invokeAndWait {
            model.bar1.setValue(current)
            model.label1.setText(label)
        }
    }

    void setIntermediateOverall(int min, int max, String stepLabel, String label) {
        log.debug("intermediate set to ${min} - ${max} ${stepLabel} ${label}")
        SwingUtilities.invokeAndWait {
            model.bar2.setMinimum(min)
            model.bar2.setMaximum(max)
            model.stepLabel.setText(stepLabel)
            model.label2.setText(label)
        }
    }

    void setIntermediateProgress(int current, String stepLabel, String label) {
        log.trace("intermediate set to ${current} ${stepLabel} ${label}")
        SwingUtilities.invokeAndWait {
            model.bar2.setValue(current)
            model.stepLabel.setText(stepLabel)
            model.label2.setText(label)
        }
    }

    void setDetailOverall(int min, int max) {
        log.debug("Detail Progress set to ${min} ${max}")
        SwingUtilities.invokeAndWait {
            model.bar3.setMinimum(min)
            model.bar3.setMaximum(max)
        }
    }

    void setDetailProgress(int current, String label) {
        log.trace("detail Progress set to ${current} with ${label}")
        SwingUtilities.invokeAndWait {
            model.bar3.setValue(current)
            model.label3.setText(label)
        }
    }

}
