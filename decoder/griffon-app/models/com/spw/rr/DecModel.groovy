package com.spw.rr

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.EventList
import com.spw.rr.mappers.DecoderEntry
import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Observable

@ArtifactProviderFor(GriffonModel)
class DecModel extends RRBaseModel {
    {
        columnNames.addAll(["Id", "Roster", "Profile?", "Details?", "File", "Road Name", "Road Number",
                            "Manufacturer", "Owner", "Address", "Updated"])
        preferredWidths.addAll([10, 10, 5, 5, 40, 30, 30, 40, 30, 20, 40])
    }

    public enum WindowAction {
        LIST_ALL, LIST_BY_SELECTION, CLOSE, SHOW, NONE
    }

    ArrayList<DecoderEntry> completeList = new ArrayList<>()

    int[] rosterList = null

    WindowAction currentAction = WindowAction.NONE

    @Observable
    boolean enableResetFilters = false

    @Observable
    boolean filterNoSpeed = true

    @Observable
    boolean filterSpeed = true

    @Observable
    boolean filterNoDetails = true

    @Observable
    filterDetails = true

    @Observable
    String cvDisplay = ""

    @Observable
    boolean enableCVdetail

}
