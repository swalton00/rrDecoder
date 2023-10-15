package com.spw.rr.mappers

import groovy.transform.ToString

@ToString(includeFields = true, includePackage = false, includeNames = true)
class StandardCVs {
    Integer id
    Integer decoderId
    Integer[] cvValues
}
