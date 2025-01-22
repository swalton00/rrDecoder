package com.spw.rr.database

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includeFields = true)
class DecoderDef {
    Integer parent
    Integer id
    String  varValue
    String  item
}