package com.spw.rr

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonController)
class DecoderController {
    @ControllerAction
    void importAction() {
        println("got here")
    }

    @ControllerAction
    void exitAction() {

    }

    @ControllerAction
    void helpAction() {

    }

    @ControllerAction
    void aboutAction() {
        
    }
}