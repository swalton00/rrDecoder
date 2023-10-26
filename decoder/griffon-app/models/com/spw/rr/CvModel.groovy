package com.spw.rr

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.EventList
import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class CvModel extends RRBaseModel {
    String[] columnNames = ["Decoder Id\n \n ", "DCC Address", "CV 1\nShort Address", "CV 2\nStart Volts", "CV 3\nAcceleration",
                            "CV 4\nDeceleration", "CV 5\nVHigh", "CV 6\nVmid", "CV 7\nVersion Number", "CV 8\nManufacturer",
                            "CV 9\nPWM Period","CV 10\nBEMF Cutout", "CV 11\nPacket Time", "CV 12\nPower Source",
                            "CV 13\nDC F1-F8", "CV 14\nDC F0,F9-F12", "CV 15\nDecoder Key", "CV 16\nDecoder Lock",
                            "CV 17\nAddress High", "CV 18\nAddress Low", "CV 19\nConsist"]
    String[] rowZero = ["<none>", "Short Address", "Start Volts", "Acceleration", "Deceleration", "VHigh",
          "Vmid", "Version Number", "Manufacturer", "PWM Period", "BEMF Cutout", "Packet Time",
          "Power Source", "DC F1-F8", "DC F0,F9-F12", "Decoder Key", "Decoder Lock", "Address High",
           "Address Low", "Consist"]

    int[] preferredWidths = [10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10]

    EventList<ArrayList<String>> tableList = new BasicEventList<ArrayList<String>>()

    ArrayList<Integer> selectedRows = new ArrayList<>()

    int[] rosterIds

}
