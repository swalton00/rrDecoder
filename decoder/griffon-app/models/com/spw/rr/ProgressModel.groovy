package com.spw.rr

import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import groovy.beans.Bindable
import javax.swing.JProgressBar


@Bindable
@ArtifactProviderFor(GriffonModel)
class ProgressModel {

    Integer currentValue = 0
    String currentText = ""
    String phaseCount = ""

    Integer max = 0

    String toBeProcessed = ""

    Integer phaseMaximum = 0

    Integer phaseValue = 0

    Integer subCurrent = 0

    Integer subMax = 0

    JProgressBar detailProgress = new JProgressBar(javax.swing.SwingConstants.HORIZONTAL, 0, 0)

    JProgressBar phaseProgress = new JProgressBar(javax.swing.SwingConstants.HORIZONTAL, 0, 0)

    JProgressBar subProgress = new JProgressBar(javax.swing.SwingConstants.HORIZONTAL, 0, 0)

    /**
     *  Sets the values to be processed by decoder import, either regular or detailed
     *  Also ensures that all the setting is done on the gui thread
     * @param processCount The number of decoders to be processed
     * @param phaseCount The total number of phases for this process
     */
    void setTotals(int processCount, int phaseCount) {
        toBeProcessed = String.valueOf(processCount)
        max = processCount
        currentValue = 0
        this.phaseMaximum = phaseCount
        phaseValue = 0
        subCurrent = 0
        subMax = 0
    }

    /*
        The processing for a Decoder imports
           The overall count is the number of locomotive elements within the roster.xml
           There are 4 phases:
               1 - Import the roster
               2 - Function entries
               3 - Key value pairs
               4 - Speed profile entries if any
          For detailed imports, there are 3 phases:
               1 - Read and parse the XML
               2 - process the DecoderDef entries
               3 - process the CV values
     */

    /**
     * Set the process current count to the value passed in
     * ensures that this is run on the gui thread
     * @param current the decoder number within the process
     * @param phaseMax the maximum value for this phase
     */
    void setCurrent(int current, int phaseMax) {
        this.currentValue = current
        this.phaseValue = 0
        this.phaseMaximum = phaseMax
    }

    /**
     * Set the progress values for the current phase
     * Also starts a new sub process counter
     * @param current The current phase of processing
     * @param subMax the maximum value for the sub process bar
     */
    void setPhaseCurrent(int current, int subMax) {
        this.phaseValue = current
        this.subMax = subMax
    }

    /**
     * Set the progress bar for the detailed processing with the process
     * @param current current value of the sub counts
     */
    void setSubCurrent(int current) {
        this.subCurrent = current
    }

}
