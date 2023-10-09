package com.spw.rr

import com.spw.rr.DecModel
import com.spw.rr.ProgressModel
import com.spw.rr.RRTableModel
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import net.miginfocom.swing.MigLayout

import javax.annotation.Nonnull
import javax.swing.JProgressBar
import javax.swing.SwingConstants

@ArtifactProviderFor(GriffonView)
class ProgressView {
    @MVCMember
    @Nonnull
    FactoryBuilderSupport builder

    @MVCMember
    @Nonnull
    ProgressModel model


    void initUI() {
        builder.with {
            application(size: [500, 200], id: 'progress',
                    title: 'Decoder Progress') {
                migLayout()
                label("Detail Processing:", constraints: 'newline')
                model.detailProgress = new JProgressBar(SwingConstants.HORIZONTAL, 0, 0)
            }
        }
    }
}