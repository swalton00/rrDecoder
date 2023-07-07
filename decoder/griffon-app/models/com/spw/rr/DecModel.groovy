package com.spw.rr

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.EventList
import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class DecModel extends RRBaseModel {
    String[] columnNames = ["Roster", "Id", "File", "Road Name", "Road Number", "Manufacturer", "Owner", "Address", "Updated"]

    int[] preferredWidths = [10, 10, 40, 30, 30, 40, 30, 20, 40]

    EventList<ArrayList<String>> tableList = new BasicEventList<ArrayList<String>>()

    public enum WindowAction {
        LIST_ALL, LIST_BY_SELECTION, CLOSE, SHOW, NONE
    }

    ArrayList<Integer> selectedRows = new ArrayList<>()

    WindowAction currentAction = WindowAction.NONE
}
