package com.spw.rr.database

import groovy.transform.ToString

import java.sql.Timestamp
@ToString(includeNames = true, includePackage = false, includeFields = true)
class VersionBase {

    Integer decoderId
    String  key
    Integer versionNumber
    Timestamp createdOn

    enum WhichTable {
        CV,
        LABEL,
        KEYVALUE
    }

    WhichTable tableSource
    boolean hasBeenWritten = false
}
