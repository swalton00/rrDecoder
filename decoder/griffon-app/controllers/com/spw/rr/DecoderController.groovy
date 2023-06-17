package com.spw.rr

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.core.mvc.MVCGroup
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javax.annotation.Nonnull
import javax.swing.JFileChooser

@ArtifactProviderFor(GriffonController)
class DecoderController {
    private static importer = new ImportDecoderList()

    MVCGroup prefsGroup = null

    @ControllerAction
    void importAction() {
        log.info("Importing a JMRI collection - choosing file now")
        JFileChooser chooser = new JFileChooser()
        chooser.setDialogTitle("Select JMRI decoder index")
        int retVal = chooser.showOpenDialog(null)
        if (retVal == JFileChooser.APPROVE_OPTION) {
            importer.importList(chooser.getSelectedFile())
        }

    }

    private checkGroup(String groupName, MVCGroup group) {
        if (group == null) {

        }
    }

    @ControllerAction
    void exitAction() {
        log.debug("Shutting down now")
        application.shutdown()
    }

    @ControllerAction
    void prefsAction() {
        prefsGroup = checkGroup("prefs", prefsGroup)
        log.debug("showing the Preferences window")
        application.getWindowManager().show("prefs")
    }


    @ControllerAction
    void helpAction() {

    }

    @ControllerAction
    void aboutAction() {

    }
}