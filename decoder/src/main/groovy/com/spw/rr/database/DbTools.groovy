package com.spw.rr.database

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement


@Singleton
class DbTools {

    private static final Logger log = LoggerFactory.getLogger(DbTools.class)

    /**
     * Check the url for validity - return empty string if good
     * @param url the url to be tested
     * @param user the userid for the connection
     * @param password the password for the connection
     * @return an empty string if good, an error message otherwise
     */
    String checkUrl(String url, String user, String password) {
        if (!url.startsWith("jdbc:")) {
            log.error("URL does not start with jdbc:")
            return "not a valid JDBC URL - must start with 'jdbc:'"
        }
        if (!(url.startsWith("jdbc:h2:") | url.startsWith("jdbc:db2:"))) {
            log.error("URL does not reflect H2 as the database")
            return "only h2 or DB2 database currently supported"
        }
        if (url.contains("jdbc:h2") & !url.contains("MODE=DB2;")) {
            log.error("URL does not contain MODE=DB2;")
            return "the h2 URL must contain the string 'MODE=DB2;' for correct operation"
        }
        if (url.contains("jdbc:db2:")) {
            try {
                log.debug("loading IBM DB2 Driver")
                Class.forName("com.ibm.db2.jcc.DB2Driver")
            } catch (ClassNotFoundException e) {
                log.error("DB2 Driver class not found", e)
            }
        }
        log.debug("about to try connecting with that URL - ${url}")
        Connection conn
        try {
            conn = DriverManager.getConnection(url, user, password)
            Statement stmt = conn.createStatement()
            if (url.startsWith("jdbc:h2:")) {
                log.debug("using h2 format SQL statement")
                boolean res = stmt.execute("Select count(*) from INFORMATION_SCHEMA.SCHEMATA")
            } else if (url.startsWith("jdbc:db2:")) {
                log.debug("using DB2 format SQL statement")
                boolean res = stmt.execute("Select count(*) from SYSIBM.SYSTABLES where CREATOR = 'RRDEC'")
            }
            log.debug("successfully connected to the database")
        } catch (SQLException sqlex) {
            log.error("SQL Exception - ", sqlex)
            return sqlex.getMessage()
        } catch (Exception e) {
            log.error("Exception other than SQL Exception - ", e)
            return e.getMessage()
        } finally {
            if (conn != null)
                conn.close()
        }
        return ""
    }
}
