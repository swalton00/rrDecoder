package com.spw.rr.database

import groovy.transform.ToString

@ToString(includeFields = true, includePackage = false, includeNames = true)
class CVvalues {
    Integer id
    Integer decoderId
    String  cvNumber
    String  cvValue
    String  dccAddress      // included for purposes of some retrievals
}
