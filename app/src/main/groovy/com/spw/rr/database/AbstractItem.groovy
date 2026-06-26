package com.spw.rr.database

import com.spw.rr.controllers.DataController.ViewType
import groovy.util.logging.Log4j
import org.slf4j.Logger

@Log4j
abstract class AbstractItem {

    Integer decoderId

    abstract String  getKey();

    abstract void addRows(List<String> thisLine);

    abstract String getValue();

    abstract void setValue(String newValue);

}
