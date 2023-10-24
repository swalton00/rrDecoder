package com.spw.rr

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.EventList
import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class CvModel extends RRBaseModel {
    String[] columnNames = ["Decoder Id", "CV 1", "CV 2", "CV 3", "CV 4", "CV 5", "CV 6", "CV 7", "CV 8",
          "CV 10", "CV 11", "CV 12", "CV 13", "CV 14", "CV 15", "CV 16", "CV 17", "CV 18", "CV 19"]
    String[] rowZero = ["<none>", "Short Address", "Start Volts", "Acceleration", "Deceleration", "VHigh",
          "Vmid", "Version Number", "Manufacturer", "PWM Period", "BEMF Cutout", "Packet Time",
          "Power Source", "DC F1-F8", "DC F0,F9-F12", "Decoder Key", "Decoder Lock", "Address High",
           "Address Low", "Consist"]

    int[] preferredWidths = [10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10]

    EventList<ArrayList<String>> tableList = new BasicEventList<ArrayList<String>>()

}
