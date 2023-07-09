package com.spw.rr.mappers

import groovy.transform.ToString

import java.sql.Timestamp

@ToString(includeNames = true, includePackage = false, includeFields = true)
class DecoderEntry {
    Integer id
    String decoderId
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
    Integer decoderTypeId
    Integer rosterId
}
