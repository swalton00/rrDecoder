package com.spw.rr.database

import com.spw.rr.controllers.DataController
import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includeFields = true)
class DecoderDef extends AbstractItem{
    Integer parent
    Integer id
    Integer decoderId
    String  varValue
    String  item

    @Override
    String getKey() {
        return varValue
    }

    @Override
    void addRows(List<String> thisLine) {
        thisLine.add(item)
    }
}