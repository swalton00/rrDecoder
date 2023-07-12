package com.spw.rr.mappers

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includeFields = true)
class FunctionLabel {
    Integer id
    Integer decoderId
    Integer functionNum
    String  functionLabel
    Integer rosterId
}
