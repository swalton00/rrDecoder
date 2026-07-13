package com.spw.rr.database

import groovy.transform.ToString

class KeyValuePairs extends AbstractItem{

    Integer id
    String  pair_key

    @Override
    void setNewValue(AbstractDiff diff) {
        if (!(diff instanceof KeyDiff)) {
            log.error("Setting new value on diff of wrong type ${diff}")
            throw new RuntimeException("Setting new value on wrong type of diff ${diff}")
        }
        ((KeyDiff)diff).newValue = pair_value
    }
    String pair_value

    @Override
    String getKey() {
        return pair_key
    }

    @Override
    void setKey(String key) {
        pair_key = key
    }

    @Override
    void addRows(List<String> thisLine) {
        thisLine.add(pair_value)
    }

    @Override
    String getValue() {
        return pair_value
    }

    void setValue(String newValue) {
        pair_value = newValue
    }

    @Override
    void setOldValue(AbstractDiff diff) {
        if (!(diff instanceof KeyDiff)) {
            log.error("KeyValuePair asked to set incorrect diff type")
            throw new RuntimeException("Incorrect type for diff ${diff}")
        }
        ((KeyDiff)diff).oldValue = pair_value
    }




}
