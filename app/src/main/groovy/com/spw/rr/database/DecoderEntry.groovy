package com.spw.rr.database

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
    Timestamp detailTime
    Integer decoderTypeId
    Integer rosterId
    String hasDetail
    String hasSpeedProfile

    List<CvValues> cvValues
}

