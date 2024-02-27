package com.spw.rr

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.EventList
import com.spw.rr.mappers.DecoderEntry
import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import javafx.beans.Observable
import griffon.transform.Observable

@ArtifactProviderFor(GriffonModel)
class CvSpecificModel extends RRBaseModel {

    {
        columnNames.addAll(["ID", "CV"])
        preferredWidths.addAll([15, 5])
    }

    @Observable String windowTitle = "Specific CV Values"

    String cvList = ""

    ArrayList<DecoderEntry> completeList = new ArrayList<>()
}