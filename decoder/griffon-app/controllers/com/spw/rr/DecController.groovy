package com.spw.rr

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonController)
class DecController {

    @ControllerAction
    mainAction() {

    }

    @ControllerAction
    helpAction() {

    }
}
