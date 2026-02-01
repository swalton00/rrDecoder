package com.spw.rr.database

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true, includeFields = true)
class SavedLabel extends SaverBase{
    String functionNumber
    String saved_label
    boolean  locked

    @Override
    void setKey(String newKey) {
        functionNumber = newKey
    }

    @Override
    void setValue(String newValue) {
        saved_label = newValue
    }

    @Override
    String getKey() {
        return functionNumber
    }

    @Override
    String getValue() {
        return saved_label
    }
}
