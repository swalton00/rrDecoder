package com.spw.rr.views

import com.spw.rr.database.AbstractDiff

import java.sql.Timestamp

abstract class AbstractViewDiff extends AbstractDiff{

    Timestamp created_on
    Integer   version
    String    old_value
    String new_value

}
