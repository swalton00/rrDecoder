package com.spw.rr

import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Observable

@ArtifactProviderFor(GriffonModel)
class PrefsModel {
    @Observable String username = ""
    @Observable String password = ""
    @Observable String url = ""
    @Observable Boolean fileSelected = false
    @Observable Boolean urlSelected = true
    @Observable String fileName = ""
    @Observable String dbName = ""
    @Observable Boolean okayEnabled = false
    @Observable Boolean propertiesValid = false // saved properties are good
    @Observable String errorMessage = ""
    boolean prefsChanged = false
    Boolean goodUsername = false
    Boolean goodPassword = false
    Boolean goodDbname = false
    Boolean goodlocation = false
    Boolean goodurl = false
}