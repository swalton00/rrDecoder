package com.spw.rr.database

import com.spw.rr.controllers.DataController
import groovy.transform.ToString

@ToString(includeFields = true, includePackage = false, includeNames = true)
class CvValues extends AbstractItem{
    Integer id
    Integer decoderId
    String  cvNumber
    String  cvValue
    String  dccAddress      // included for purposes of some retrievals

    @Override
    String getKey() {
        return cvNumber
    }

    @Override
    void addRows(List<String> thisLine) {
        thisLine.add(cvValue)
    }
}
