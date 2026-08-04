package com.spw.rr.database

import groovy.transform.ToString
import java.sql.Timestamp

@ToString(includeFields = true, includePackage = false, includeNames = true)
abstract class AbstractDiff {

    Integer decoderId
    Integer versionNumber
    String oldValue
    String newValue
    Timestamp createdOn
    VersionBase.WhichTable tableSource

    abstract String getKey();

    abstract void setKey(String key);

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
