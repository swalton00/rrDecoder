package com.spw.rr.database

import groovy.transform.ToString

@ToString(includeFields = true, includeNames = true, includePackage = false)
class LabelDiff extends AbstractDiff{

    Boolean oldLocked
    Boolean newLocked
    String functionNumber

    Boolean getOldLocked() {
        return oldLocked
    }

    Boolean getNewLocked() {
        return newLocked
    }

    @Override
    String getKey() {
        return functionNumber
    }

    @Override
    boolean wasChanged() {
        if (oldLocked != null) {
            if (newLocked == null) {
                return true
            } else {
                if (oldLocked ^ newLocked) {
                    return true
                } else return super.wasChanged()
            }
        } else {
            return true
        }
    }
}
