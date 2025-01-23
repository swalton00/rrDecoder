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

    private static final int D_WIDTH = 300
    private static final int D_HEIGHT = 400

    SeeProgressView(Component parent, SeeProgressController controller, SeeProgressModel model) {
        this.parent = parent
        this.controller = controller
        this.model = model
    }

    void init() {
        SwingUtilities.invokeLater {
            model.theDialog = new JDialog(parent, "Import Progress", true)
            model.theDialog.getContentPane().setLayout(new MigLayout("fill"))
            model.theDialog.setLayout(new MigLayout("fill"))
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
            model.theDialog.getContentPane().add(mainPanel, "center, grow")
            model.theDialog.setSize(D_WIDTH, D_HEIGHT)
            Toolkit toolkit = Toolkit.getDefaultToolkit()
            int x = (toolkit.getScreenSize().width - D_WIDTH) / 2
            int y = (toolkit.getScreenSize().height - D_HEIGHT) / 2
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
