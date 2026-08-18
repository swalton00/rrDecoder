package com.spw.rr.utilities

import com.spw.rr.database.FunctionLabel
import com.spw.rr.database.ImportMapper
import com.spw.rr.database.RosterEntry
import com.spw.rr.database.VersionBase
import org.apache.ibatis.session.SqlSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Timestamp

@Singleton
class ImportDb {
    private static final Logger log = LoggerFactory.getLogger(ImportDb.class)


    DatabaseServices parent = null

    private void preTest() {
         if (parent == null) {
            parent = DatabaseServices.getInstance()
        }
    }

    RosterEntry addRoster(RosterEntry entry) {
        preTest()
        log.debug("adding a new RosterEntry ${entry}")
        SqlSession session
        try {
            session = parent.sqlSessionFactory.openSession(true)
            ImportMapper map = session.getMapper(ImportMapper.class)
            map.insertRosterEntry(entry)
        } finally {
            if (session) {
                session.close()
            }
        }
        return entry
    }


    Object executeSql(  Closure method, int decoderId)
    {
        SqlSession session
        Object retVal = null
        try {
            session = parent.sqlSessionFactory.openSession(true)
            ImportMapper map = session.getMapper(ImportMapper.class)
            retVal = (Object)method(map, decoderId)
        } finally {
            if (session) {
                session.close()
            }
        }
        return retVal
    }

    void executeWrite( Closure method) {
        SqlSession session = null
        try {
            ImportMapper map = session.openSession(true)
            method(map)
        } finally {
            if (session != null) {
                session.close()
            }
        }
    }

}
