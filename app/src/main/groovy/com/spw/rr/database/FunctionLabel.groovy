package com.spw.rr.database

import groovy.transform.ToString
import groovy.util.logging.Log4j
import org.slf4j.LoggerFactory

@ToString(includeNames = true, includePackage = false, includeFields = true)
class FunctionLabel extends AbstractItem {

    Integer id
    String functionNum
    String  functionLabel
    boolean locked
    Integer rosterId
    Integer labelVersion

    @Override
    boolean equals(Object other) {
        log.debug("invoked Function Label instance")
        if (!(other instanceof FunctionLabel)) {
            return false
        }
         if ((locked &! other.locked) |
                 (!locked & other.locked)) {
             return false
         }
        if (!decoderId.equals(other.decoderId)) return false
        if (!functionNum.equals(other.functionNum)) return false
        if (!(functionLabel.equals(other.functionLabel))) return false
        return true
    }

    String getKey() {
        return functionNum.toString()
    }

    void addRows(List<String> thisLine) {
        thisLine.add(functionLabel)
    }

    String getValue() {
        return functionLabel
    }
}
