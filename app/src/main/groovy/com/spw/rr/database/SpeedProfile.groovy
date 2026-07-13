package com.spw.rr.database

import groovy.transform.ToString
import groovy.util.logging.Log4j

class SpeedProfile extends AbstractItem {
    Integer id
    Integer speedStep
    Double  forwardValue
    Double  reverseValue
    Integer rosterId

    @Override
    String getKey() {
        return speedStep.toString()
    }

    void setKey(String key) {
        throw new RuntimeException("Cannot set the key of Speed Profile item")
    }

    @Override
    void addRows(List<String> thisLine) {
        thisLine.add(String.format("%.3f", forwardValue))
        thisLine.add(String.format("%.3f", reverseValue))
    }

    @Override
    String getValue() {
        return forwardValue.toString() + reverseValue.toString()
    }

    void setValue(String newValue) {
        log.error("Speed Profiles should not be receiving setValue, getValue requests")
    }

    @Override
    void setOldValue(AbstractDiff diff) {
        log.error("Speed profiles should not invoke Diffs")
    }

    @Override
    void setNewValue(AbstractDiff diff) {
        log.error("Speed profiles should not invoke Diffs")
    }
}