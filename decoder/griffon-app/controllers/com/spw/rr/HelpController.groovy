package com.spw.rr

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading

import javax.inject.Inject

@ArtifactProviderFor(GriffonController)
class HelpController {

    @MVCMember
    HelpModel model

    @Inject
    DecoderDBService database

    @ControllerAction
    void closeAction() {
        log.debug("close has been requested")

    }

    @ControllerAction
    void helpAction() {
        log.debug("help has been requested")
    }

    boolean inited = false

    void onWindowShow(String name, Object window) {
        if (name.equals("help")) {

        }
    }

}
