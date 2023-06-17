package com.spw.rr

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.core.mvc.MVCGroup
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javax.annotation.Nonnull
import javax.swing.JFileChooser

@ArtifactProviderFor(GriffonController)
class PrefsController {

    void onWindowShown(String name, Object window) {
        if (name.equals("prefs")) {
            log.debug("our window is being shown")
        }

    }

}
