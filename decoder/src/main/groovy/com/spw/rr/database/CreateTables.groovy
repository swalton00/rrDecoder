package com.spw.rr.database

import griffon.transform.ResourcesAware
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection

@Singleton
@ResourcesAware
class CreateTables {
    private static Logger log = LoggerFactory.getLogger(CreateTables.class)

    void createTables(Connection conn) {
        log.debug("about to create the tables")
        ApplyResources executor = new ApplyResources()
        executor.excuteSQLResource(conn, "createTables.sql")
    }

}
