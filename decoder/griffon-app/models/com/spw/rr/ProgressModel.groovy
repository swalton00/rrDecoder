package com.spw.rr

import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Observable
import groovy.beans.Bindable

import javax.swing.JProgressBar

@Bindable
@ArtifactProviderFor(GriffonModel)
class ProgressModel {

    Integer phase = 0

    Integer phaseMax = 0

    Integer current = 0

    Integer max = 0

    JProgressBar phaseProgress
    JProgressBar detailProgress
}
