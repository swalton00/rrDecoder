package com.spw.rr

import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Observable
import groovy.beans.Bindable

import javax.swing.JProgressBar

@Bindable
@ArtifactProviderFor(GriffonModel)
class ProgressModel {

    Integer current = 0

    Integer max = 0

    JProgressBar detailProgress = new JProgressBar(javax.swing.SwingConstants.HORIZONTAL, 0, 0)
}
