package com.spw.rr.utilities

import com.spw.rr.database.FunctionLabel
import com.spw.rr.database.ImportMapper
import com.spw.rr.database.Mapper
import com.spw.rr.database.RosterEntry
import com.spw.rr.database.SaverObject
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

    ArrayList<FunctionLabel> getFunctionLabelsFor(int decoderId) {
        log.debug("getting a list of FunctionLabels for decoderId ${decoderId}")
        ArrayList<FunctionLabel> retVal  = executeSql({int id ->
            return mapper.getFunctionLabels( id)
        }, decoderId)
        return retVal
    }

    ArrayList<SaverObject> executeSql(  Closure method, int decoderId)
    {
        SqlSession session
        ArrayList<SaverObject> retVal
        try {
            session = parent.sqlSessionFactory.openSession(true)
            ImportMapper map = session.getMapper(ImportMapper.class)
            retVal = (ArrayList<SaverObject>)method(decoderId)
        } finally {
            if (session) {
                session.close()
            }

        }
        return retVal
    }
}
