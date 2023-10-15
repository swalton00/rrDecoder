package com.spw.rr

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading

import javax.inject.Inject

@ArtifactProviderFor(GriffonController)
class CvController {

    @MVCMember
    CvModel model

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

    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    void onCvWindow() {
        log.debug("got a CV Window event - showing the list from the model")
    }

    boolean inited = false

    void onWindowShow(String name, Object window) {
        if (name.equals("")) {

        }
    }


}
