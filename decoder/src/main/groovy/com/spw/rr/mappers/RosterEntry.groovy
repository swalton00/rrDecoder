package com.spw.rr.mappers

import groovy.transform.ToString

import java.sql.Timestamp

@ToString(includeNames = true, includePackage = false, includeFields = true)
class RosterEntry {
    Timestamp entryTime
    String fullPath
    Integer id
    Timestamp dateUpdated
    String systemName
}
