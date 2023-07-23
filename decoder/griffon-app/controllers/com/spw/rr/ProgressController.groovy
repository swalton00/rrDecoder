package com.spw.rr

import griffon.core.artifact.GriffonController
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonController)
class ProgressController {

    @MVCMember
    ProgressModel model

    void onWindowHidden(String name, Object window) {
        log.debug("window hidden for ${name}")
        if (name.equals("mainWindow")) {
            log.debug("main widow hidden - also closing progress")
            application.getWindowManager().hide("progress")
            application.shutdown()
        }
    }
}
