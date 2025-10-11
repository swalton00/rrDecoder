package com.spw.rr.database

import groovy.transform.ToString
import org.slf4j.LoggerFactory

@ToString(includeNames = true, includePackage = false, includeFields = true)
class FunctionLabel extends SaverObject {

    FunctionLabel() {
        log = LoggerFactory.getLogger(FunctionLabel.class)
    }

    Integer id
    String functionNum
    String  functionLabel
    boolean locked
    boolean visible
    boolean shunt
    Integer rosterId

    @Override
    String getKey() {
        return functionNum.toString()
    }

    @Override
    void addRows(List<String> thisLine) {
        thisLine.add(functionLabel)
    }

    @Override
    String getValue() {
        return functionLabel
    }


    int compareTo(SaverObject otherItem) {
        if (this.decoderId != otherItem.decoderId) {
            if (this.decoderId < otherItem.decoderId) {
                return -1
            } else {
                return 1
            }
        }
        if (!(otherItem instanceof FunctionLabel)) {
            log.error("Attempting to compare a FunctionLabel to another type")
            throw RuntimeException("Attempting to compare a Function Label to something else")
        }
        if (!functionNum.equals(otherItem.functionNum)) {
             if (functionNum < otherItem.functionNum) {
                 return -1
             } else {
                 return 1
             }
         }
        if (functionLabel < otherItem.functionLabel) {
            return -1
        } else {
            return 1
        }
        return 0
    }


    boolean equals(Object otherItem) {
        if (!otherItem instanceof FunctionLabel) {
            return false
        }
        if (!functionNum.equals(otherItem.functionNum)) {
            return false
        }
        return functionLabel.equals(otherItem.functionLabel)
    }

}
