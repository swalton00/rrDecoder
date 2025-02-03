package com.spw.rr.database

import groovy.transform.ToString

@ToString(includeNames = true,includePackage = false, includeFields = true)
class KeyValuePairs extends AbstractItem{

    Integer id
    Integer decoderId
    String  pair_key
    String  pair_value

    @Override
    String getKey() {
        return pair_key
    }

    @Override
    void addRows(List<String> thisLine) {
        thisLine.add(pair_value)
    }
}
