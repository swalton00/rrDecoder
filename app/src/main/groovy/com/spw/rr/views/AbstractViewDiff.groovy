package com.spw.rr.views

import com.spw.rr.database.AbstractDiff
import groovy.transform.ToString

import java.sql.Timestamp

@ToString(includeNames = true, includePackage = false, includeFields = true)
abstract class AbstractViewDiff extends AbstractDiff{
    Integer decoderId
    Timestamp created_on

    abstract String getKey()

    abstract void setKey(String newKey)

}
