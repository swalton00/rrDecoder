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

    String[] columnNames = ["Id", "System", "# Decoders", "Full Path"]

    int[] preferredWidths = [10,20,300]

    EventList<ArrayList<String>> tableList = new BasicEventList<ArrayList<String>>()
    
    @Observable
    boolean importDetailEnabled = false

    ArrayList<Integer> selectedRows = new ArrayList<>()
}