package com.spw.rr

import griffon.core.GriffonApplication
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import com.spw.rr.PropertiesService

import javax.annotation.Nonnull
import javax.inject.Inject

@ArtifactProviderFor(GriffonController)
class PrefsController {

    @MVCMember @Nonnull
    PrefsModel model

    @Inject
    private
    PropertiesService props

    void onWindowShown(String name, Object window) {
        if (name.equals("prefs")) {
            log.debug("our window is being shown")
            model.username = props.getPropertyUser()
            model.password = props.getPropertyPassword()
            model.url = props.getPropertyURL()
        }

    }

    void onShutdownStart(GriffonApplication application) {
        log.debug("in shutdown start for the controller")
    }

    private void closeThisWindow() {
        application.getWindowManager().hide("prefs")
    }

    @ControllerAction
    void okayAction() {
        props.storeUser(model.username)
        props.storePassword(model.password)
        props.storeURL(model.url)
        props.saveProperties()
        closeThisWindow()
    }

    @ControllerAction
    void cancelAction() {
        closeThisWindow()
    }

}
