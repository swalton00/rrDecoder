package com.spw.rr

import groovy.transform.ToString

import java.sql.Timestamp

@ToString(includeNames = true, includePackage = false, includeFields = true)
class LocomotiveEntry {
    Integer id
    String locoId
    String fileName
    String roadNumber
    String roadName
    String manufacturer
    String owner
    String model
    String dccAddress
    String manufacturerId
    String productId
    Timestamp dateUpdated
    Integer decoderId
    Integer rosterId
}
