package com.spw.rr


import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor

import javax.annotation.Nonnull

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
                label("To be processed:", constraints:("align right"))
                label(text: bind {model.toBeProcessed}, constraints:("wrap"))
                label("Current decoder:", constraints: "align right")
                label(text: bind {model.currentText}, constraints: "wrap")
                label("Decoder Processing:", constraints: "align right")
                model.detailProgress = progressBar(maximum: bind{model.max}, value: bind{model.currentValue},
                     constraints: "wrap")
                label("Phases:", constraints:("align right"))
                model.phaseProgress = progressBar(maximum: bind{model.phaseMaximum}, value: bind{model.phaseValue},
                     constraints: "wrap")
                label("Phase Progress", constraints: "align right")
                label(text: bind{model.phaseCount}, constraints:("wrap"))
                label("Detailed progress", constraints:("align right"))
                model.subProgress = progressBar(maximum: bind{model.subMax}, value: bind{model.subCurrent})
            }
        }

    }
}