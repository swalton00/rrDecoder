package com.spw.rr.views

import com.spw.rr.controllers.SeeProgressController
import com.spw.rr.models.SeeProgressModel
import net.miginfocom.swing.MigLayout

import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities
import java.awt.Component
import java.awt.Font
import java.awt.Toolkit

class SeeProgressView {

    SeeProgressController controller
    SeeProgressModel model
    Component parent

    SeeProgressView(Component parent, SeeProgressController controller, SeeProgressModel model) {
        this.parent = parent
        this.controller = controller
        this.model = model
    }

    void init() {
        SwingUtilities.invokeLater {
            model.theDialog = new JDialog(parent, "Import Progress", true)
            model.theDialog.setLayout(new MigLayout())
            JPanel mainPanel = new JPanel(new MigLayout())
            JLabel panelTitle = new JLabel("Import Progress")
            Font currentFont = panelTitle.getFont()
            Font newFont = new Font(currentFont.getFontName(), Font.BOLD, 24)
            mainPanel.add(panelTitle, "center, wrap")
            mainPanel.add(model.bar1, "center, wrap")
            mainPanel.add(model.label1, "center, wrap")
            mainPanel.add(model.bar2, "center, wrap")
            mainPanel.add(model.stepLabel, "center, wrap")
            mainPanel.add(model.label2, "center, wrap")
            mainPanel.add(model.bar3, "center, wrap")
            mainPanel.add(model.label3, "center, wrap")
            model.theDialog.getContentPane().add(mainPanel, "grow")
            model.theDialog.setSize(500, 600)
            Toolkit toolkit = Toolkit.getDefaultToolkit()
            int x = (toolkit.getScreenSize().width - 500) / 2
            int y = (toolkit.getScreenSize().height - 600) / 2
            model.theDialog.setLocation(x, y)
            model.theDialog.setVisible(true)
        }
    }

    void show() {
       // model.theDialog.setVisible(true)
    }

    void setComplete() {
        SwingUtilities.invokeAndWait {
            model.theDialog.setVisible(false)
        }
    }
}
