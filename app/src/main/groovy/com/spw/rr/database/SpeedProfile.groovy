package com.spw.rr.database

import groovy.transform.ToString

@ToString(includeFields = true,includePackage = false, includeNames = true)
class SpeedProfile {
    Integer id
    Integer decoderId
    Integer speedStep
    Double  forwardValue
    Double  reverseValue
    Integer rosterId
}