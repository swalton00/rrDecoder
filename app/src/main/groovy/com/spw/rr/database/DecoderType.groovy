package com.spw.rr.database

import groovy.transform.ToString

import java.sql.Timestamp

@ToString(includeNames = true, includePackage = false, includeFields = true)
class DecoderType {
    Integer id
    String decoderModel
    String decoderFamily
    Timestamp updated
}
