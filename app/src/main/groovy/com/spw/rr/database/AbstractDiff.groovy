package com.spw.rr.database

abstract class AbstractDiff {

    Integer decoderId
    Integer versionNumber
    String oldValue
    String newValue

    abstract String getKey();

    boolean wasChanged() {
        if (oldValue != null) {
            if (oldValue.equals(newValue)) {
                return false
            } else {
                return true
            }
        } else {
            return true // old and new cannot both be null
        }
    }

}
