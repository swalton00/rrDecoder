package com.spw.rr.database

abstract class SaverBase {
    Integer decoderId
    Integer version

   abstract void setKey(String newKey)
    abstract void setValue(String newValue)
    abstract String getKey()
    abstract String getValue()
}
