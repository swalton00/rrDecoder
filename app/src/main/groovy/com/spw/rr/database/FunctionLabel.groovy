package com.spw.rr.database

import groovy.transform.ToString
import groovy.util.logging.Log4j
import org.slf4j.LoggerFactory

@Log4j
@ToString(includeNames = true, includePackage = false, includeFields = true)
class FunctionLabel extends AbstractItem {

    Integer id
    String functionNum
    String  functionLabel
    boolean locked
    Integer rosterId

    @Override
    boolean equals(Object other) {
        log.debug("invoked Function Label instance")
        if (!(other instanceof FunctionLabel)) {
            return false
        }
         if ((locked &! other.locked) |
                 (!locked & other.locked)) {
             return false
         }
        if (!decoderId.equals(other.decoderId)) return false
        if (!functionNum.equals(other.functionNum)) return false
        if (!(functionLabel.equals(other.functionLabel))) return false
        return true
    }

    String getKey() {
        return functionNum.toString()
    }

    void setKey(String key) {
        functionNum = key
    }

    void addRows(List<String> thisLine) {
        thisLine.add(functionLabel)
    }

    String getValue() {
        return functionLabel
    }

    void setValue(String newValue) {
        functionLabel = newValue
    }

    @Override
    void setOldValue(AbstractDiff diff) {
        if (!(diff instanceof LabelDiff) ) {
            log.error("FunctionLabel was asked to set old in the wrong type of Diff")
            throw new RuntimeException("Wrong type of diff passed to FunctionLabel ${diff}")
        }
        ((LabelDiff)diff).oldValue = functionLabel
        ((LabelDiff)diff).oldLocked = locked
    }

    @Override
    void setNewValue(AbstractDiff diff) {
        if (!(diff instanceof LabelDiff) ) {
            log.error("FunctionLabel was asked to set old in the wrong type of Diff")
            throw new RuntimeException("Wrong type of diff passed to FunctionLabel ${diff}")
        }
        ((LabelDiff)diff).newValue = functionLabel
        ((LabelDiff)diff).newLocked = locked
    }
}
