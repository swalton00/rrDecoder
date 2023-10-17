package com.spw.rr

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.EventList
import com.spw.rr.mappers.DecoderEntry
import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Observable

@ArtifactProviderFor(GriffonModel)
class DecModel extends RRBaseModel {
    String[] columnNames = ["Id", "Roster", "File", "Road Name", "Road Number", "Manufacturer", "Owner", "Address", "Updated"]

    int[] preferredWidths = [10, 10, 40, 30, 30, 40, 30, 20, 40]

    EventList<ArrayList<String>> tableList = new BasicEventList<ArrayList<String>>()

    public enum WindowAction {
        LIST_ALL, LIST_BY_SELECTION, CLOSE, SHOW, NONE
    }

    ArrayList<Integer> selectedRows = new ArrayList<>()

    ArrayList<DecoderEntry> completeList = new ArrayList<>()

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

    int[] savedList = null

}
