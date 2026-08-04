package com.spw.rr.database

import groovy.transform.ToString

import java.sql.Timestamp
@ToString(includeNames = true, includePackage = false, includeFields = true)
class VersionBase {

    Integer decoderId
    Integer versionNumber
    Timestamp createdOn
    Hashtable<Object, Object> keyValues

    enum WhichTable {
        CV,
        LABEL,
        KEYVALUE,
        UNKNOWN
    }

    VersionBase() {

    }

    VersionBase(WhichTable which) {
        this.tableSource = which
    }

    List<AbstractDiff> diffList
    WhichTable tableSource = WhichTable.UNKNOWN
    boolean hasBeenWritten = false
}
