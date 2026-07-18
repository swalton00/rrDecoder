package com.spw.rr.views

import com.spw.rr.database.AbstractItem
import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includeFields = true)
abstract class AbstractViewItem extends AbstractItem{

    Integer decoderId
    ArrayList<AbstractViewDiff> versions


}
