package com.spw.rr.database

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

class ApplyResources {

    private static final Logger log = LoggerFactory.getLogger(ApplyResources.class)

    boolean endOfStream = false

    private String  getNextStatement(InputStreamReader rdr) throws IOException {
        StringBuffer sb = new StringBuffer()
        boolean quoted = false
        boolean complete = false
        while (!complete) {
            int nextChar = rdr.read()
            if (nextChar == -1) {
                log.debug("reached end of resource stream")
                complete = true
                endOfStream = true
            } else {
                char c = nextChar
                if (c == '\'') {
                    quoted = !quoted
                    sb.append(c)
                } else if (c == ';') {
                    complete = true
                } else {
                    sb.append(c)
                }
            }
        }
        return sb.toString()
    }

    boolean excuteSQLResource(Connection conn, String resourceName) {
        log.debug("reading and executing stream {}", resourceName)
        InputStream is = this.class.getClassLoader().getResourceAsStream(resourceName)
        InputStreamReader rdr = new InputStreamReader(is)
        endOfStream = false
        try {
            while (!endOfStream) {
                String stmt = getNextStatement(rdr)
                if (stmt == null) {
                    done = true
                } else {
                    log.debug("executing: {}", stmt)
                    PreparedStatement preped = conn.prepareStatement(stmt)
                    preped.execute()
                }
            }
        } catch (SQLException sqle) {
            log.error("SQL exeception executing statement - ", sqle)
            return false
        } catch (Exception e) {
            log.error("Exeception processing", e)
            return false
        }
        return true
    }
}
