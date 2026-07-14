package com.spw.rr.database

import com.spw.rr.controllers.DataController
import groovy.transform.ToString
import groovy.util.logging.Log4j

@Log4j
@ToString(includeFields = true, includePackage = false, includeNames = true)
class CvValues extends AbstractItem{
    Integer id
    String  cvNumber
    String  cvValue
    String  dccAddress      // included for purposes of some retrievals

    @Override
    String getKey() {
        return cvNumber
    }

    void setKey(String key) {
        cvNumber = key
    }

    @Override
    void addRows(List<String> thisLine) {
        thisLine.add(cvValue)
    }

    @Override
    String getValue() {
        return cvValue
    }

    void setValue(String newValue) {
        cvValue = newValue
    }

    @Override
    void setOldValue(AbstractDiff diff) {
        if (!(diff instanceof CV_Diff)) {
            log.error("Wrong type of diff passed to setOldValue - ${diff}")
            throw new RuntimeException("Wrong type pass to CV setOldValue")
        }
        ((CV_Diff)diff).oldValue = cvValue
    }

    @Override
    void setNewValue(AbstractDiff diff) {
        if (!(diff instanceof CV_Diff)) {
            log.error("Wrong type of diff passed to setNewValue - ${diff}")
            throw new RuntimeException("Wrong type pass to CV setNewValue")
        }
        ((CV_Diff)diff).newValue = cvValue

    }
}
