package com.spw.rr.database

import com.spw.rr.controllers.DataController.ViewType
import groovy.transform.ToString
import groovy.util.logging.Log4j
import org.slf4j.Logger

@ToString(includeNames = true, includePackage = false, includeFields = true)
@Log4j
abstract class AbstractItem {

    Integer id
    Integer decoderId

    abstract String  getKey();

    abstract void addRows(List<String> thisLine);

    abstract String getValue();

    abstract void setValue(String newValue);

    abstract void setOldValue(AbstractDiff diff);

    abstract void setNewValue(AbstractDiff diff);

}
