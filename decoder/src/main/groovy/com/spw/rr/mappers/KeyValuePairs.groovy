package com.spw.rr.mappers

import groovy.transform.ToString

@ToString(includeNames = true,includePackage = false, includeFields = true)
class KeyValuePairs {

    Integer id
    Integer decoderId
    String  pair_key
    String  pair_value
}
