package com.spw.rr.database

abstract class SaverObject  extends AbstractItem {

    int decoderId

    abstract String getValue()

    abstract boolean equals(Object otherObject)

}
