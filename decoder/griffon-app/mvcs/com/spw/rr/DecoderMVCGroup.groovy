package com.spw.rr

import javax.inject.Named
import griffon.core.mvc.MVCGroup
import org.codehaus.griffon.runtime.core.mvc.AbstractTypedMVCGroup
import javax.annotation.Nonnull

@Named('decoder')
class DecoderMVCGroup extends AbstractTypedMVCGroup<DecoderModel, DecoderView, DecoderController> {
    DecoderMVCGroup(@Nonnull MVCGroup delegate) {
        super(delegate)
    }
}