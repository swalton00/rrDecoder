package com.spw.rr.database

import groovy.transform.ToString

@ToString(includeFields = true, includeNames = true, includePackage = false)
class LabelVersion extends AbstractVersion {
    String functionNumber
}
