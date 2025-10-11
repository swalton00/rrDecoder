package com.spw.rr.database

import com.spw.rr.controllers.DataController.ViewType
import org.slf4j.Logger

abstract class AbstractItem {

    abstract String  getKey();

    abstract void addRows(List<String> thisLine);

    static Logger log
}
