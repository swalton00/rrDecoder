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
    boolean prefsChanged = false
    @Observable Boolean goodUsername = false
    @Observable Boolean goodPassword = false
    @Observable Boolean goodDbname = false
    @Observable Boolean goodlocation = false
    @Observable Boolean goodurl = false
}