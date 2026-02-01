package com.spw.rr.database

import java.sql.Timestamp

abstract class AbstractVersion {
    Integer decoderId;
    Integer version;
    Timestamp version_time;
}
