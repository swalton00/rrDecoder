package com.spw.rr

import ca.odell.glazedlists.event.ListEventListener
import ca.odell.glazedlists.event.ListEventPublisher
import ca.odell.glazedlists.util.concurrent.ReadWriteLock
import griffon.core.artifact.GriffonModel
import griffon.transform.Observable
import griffon.metadata.ArtifactProviderFor

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;


@ArtifactProviderFor(GriffonModel)
class DecoderModel extends RRBaseModel {
    {

        columnNames.addAll(["Id", "System", "# Decoders", "Full Path"])
        preferredWidths.addAll([10,20,300])
    }
    
    @Observable
    boolean importDetailEnabled = false
}