package com.spw.rr

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonController)
class DecoderController {
    @MVCMember @Nonnull
    DecoderModel model

    @ControllerAction
    void click() {
        model.clickCount++
    }
}