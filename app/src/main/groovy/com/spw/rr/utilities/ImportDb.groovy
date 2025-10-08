package com.spw.rr.utilities

import com.spw.rr.database.ImportMapper
import com.spw.rr.database.Mapper
import com.spw.rr.database.RosterEntry
import org.apache.ibatis.session.SqlSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Timestamp

@Singleton
class ImportDb {
    private static final Logger log = LoggerFactory.getLogger(ImportDb.class)


    DatabaseServices parent = null

    Timestamp getCurrentDbTime() {
        if (parent == null) {
            parent = DatabaseServices.getInstance()
        }
        Timestamp retVal = null
        SqlSession session = null
        try {
            session = parent.sqlSessionFactory.openSession(true)
            ImportMapper mapper = session.getMapper(ImportMapper)
            retVal = mapper.getDBtime()
        } finally {
            if (session) {
                session.close()
            }
        }
        return retVal
    }

    RosterEntry addRoster(RosterEntry entry) {
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


    void updateRosterEntry(RosterEntry entry) {
        log.debug("updating the roster ${entry}")
        SqlSession session
        try {
            session = parent.sqlSessionFactory.openSession(true)
            ImportMapper map = session.getMapper(ImportMapper.class)
            map.updateRosterEntry(entry)
        } finally {
            if (session) {
                session.close()
            }
        }
    }


}
