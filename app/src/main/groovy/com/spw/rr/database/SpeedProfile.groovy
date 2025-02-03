package com.spw.rr.database

import groovy.transform.ToString

@ToString(includeFields = true,includePackage = false, includeNames = true)
class SpeedProfile extends AbstractItem {
    Integer id
    Integer decoderId
    Integer speedStep
    Double  forwardValue
    Double  reverseValue
    Integer rosterId

    @Override
    String getKey() {
        return speedStep.toString()
    }

    @Override
    void addRows(List<String> thisLine) {
        thisLine.add(forwardValue.toString())
        thisLine.add(reverseValue.toString())
    }
}