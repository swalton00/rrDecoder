package com.spw.rr.database

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includeFields = true)
class FunctionLabel extends AbstractItem{
    Integer id
    Integer decoderId
    Integer functionNum
    String  functionLabel
    Integer rosterId

    @Override
    String getKey() {
        return functionNum.toString()
    }

    @Override
    void addRows(List<String> thisLine) {
        thisLine.add(functionLabel)
    }
}
