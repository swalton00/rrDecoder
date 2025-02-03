package com.spw.rr.database

import com.spw.rr.controllers.DataController.ViewType

abstract class AbstractItem {

    abstract String  getKey();

    abstract void addRows(List<String> thisLine);
}
