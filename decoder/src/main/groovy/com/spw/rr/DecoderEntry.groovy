package com.spw.rr

import groovy.transform.ToString

import java.sql.Timestamp

@ToString(includeNames = true, includePackage = false, includeFields = true)
class DecoderEntry {
    Integer id
    String decoderModel
    String decoderFamily
    Timestamp updated
}
