package com.spw.rr.database

import groovy.transform.ToString

import java.sql.Timestamp
@ToString(includeNames = true, includePackage = false, includeFields = true)
class VersionBase {

    Integer decoderId
    Integer versionNumber
    Timestamp createdOn

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

    WhichTable tableSource = WhichTable.UNKNOWN
    boolean hasBeenWritten = false
}
