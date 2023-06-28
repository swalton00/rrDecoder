package com.spw.rr

import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Observable

@ArtifactProviderFor(GriffonModel)
class PrefsModel {
    @Observable String username = ""
    @Observable String password = ""
    @Observable String url = ""
}