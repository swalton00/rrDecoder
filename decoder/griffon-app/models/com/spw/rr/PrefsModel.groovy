package com.spw.rr

import griffon.core.artifact.GriffonModel
import griffon.transform.Observable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class PrefsModel {
    @Observable String username = ""
    @Observable String password = ""
    @Observable String url = ""
}
