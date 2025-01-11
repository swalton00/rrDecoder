package com.spw.rr.utilities

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

@Singleton
class DatabaseServices {

    private static final String SCHEMA_TEST = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?"
    private static final String CREATE_SCHEMA = "CREATE SCHEMA "
    private static final String TABLE_TEST =
            "SELECT COUNT(*) from information_schema.TABLES where table_schema = ? AND  TABLE_NAME IN ( ?,?,?,?,?,?,?,?,?)"
    private static final Logger log = LoggerFactory.getLogger(DatabaseServices.class)
    static String tableList = ['ROSTER', 'DECODERTYPE', 'DECODER', 'FUNCTIONLABELS',
                               'SPEEDPROFILE', 'KEYVALUES', 'DECODERDEF', 'CVVALUES', 'DB_VERSION']
    private static final String RESOURCE_NAME = "createTables.sql"

    public boolean validate(Settings settings) {
        log.debug("validating for  $Settings}")
        /*
        if url contains schema, remove schema first to validate (schema might not be present in database
        then, connect, check for schemaa (if not present,
        finally check for tables
         */
        String tempURL = settings.url
        if (tempURL.contains(";SCHEMA=")) {
            int schemaStart = tempURL.indexOf(";SCHEMA=")
            int schemaEnd = tempURL.indexOf(";",schemaStart)
            if (schemaEnd == -1) {
                tempURL = tempURL.substring(0, schemaStart -1)
                log.trace("URL without the schema is ${tempURL}")
            } else {
                String front = tempURL.substring(0, schemaStart - 1)
                String back = tempURL.substring(schemaEnd)
                tempURL = front + back
                log.trace("URL schema removed is ${tempURL}")
            }
        }
        log.debug("Testing database connection")
        Connection conn = null
        try {
            conn = DriverManager.getConnection(tempURL, settings.userid, settings.password)
            if (conn != null) {
                log.debug("connection succeeded")
                PreparedStatement stmt = conn.prepareStatement(SCHEMA_TEST)
                stmt.setString(1, settings.schema.toUpperCase())
                ResultSet rs = stmt.executeQuery()
                if (!rs.next()) {
                    log.error("result set didn't return any values")
                    throw new RuntimeException("ResultSet didn't give expected results")
                }
                int matchCount = rs.getInt(1)
                if (matchCount == 0) {
                    log.debug("schema not present -- creating")
                    PreparedStatement createSchema = conn.prepareStatement(CREATE_SCHEMA + settings.schema)
                    createSchema.execute()
                }
                PreparedStatement stmt3 = conn.prepareStatement(TABLE_TEST)
                stmt3.setString(1, settings.schema)
                tableList.eachWithIndex{ String entry, int i ->
                    stmt3.setString(i+1, entry)
                }
                ResultSet rs2 = stmt3.executeQuery()
                if (!rs2.next()) {
                    log.error("search for tables didn't return a result set as it should")
                    throw new RuntimeException("Execute query to table count didn't return a result set")
                }
                matchCount = rs2.getInt(1)
                if (matchCount == 0) {
                    log.debug("tables not found - creating")
                    ApplyResources(RESOURCE_NAME, conn)
                }
            }
        } catch (Exception e) {
            log.error("exception working with the database", e)
            throw new RuntimeException("exception working with the database")
        } finally {
            if (conn != null) {
                log.debug("closing connection")
                conn.close()
            }
        }
    }
}
