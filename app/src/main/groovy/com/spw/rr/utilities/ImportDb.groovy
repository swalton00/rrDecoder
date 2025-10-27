package com.spw.rr.utilities

import com.spw.rr.database.FunctionLabel
import com.spw.rr.database.ImportMapper
import com.spw.rr.database.LabelVersion
import com.spw.rr.database.Mapper
import com.spw.rr.database.RosterEntry
import com.spw.rr.database.SavedLabel
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
        ArrayList<FunctionLabel> retVal  = (ArrayList<FunctionLabel>)executeSql({ImportMapper map, int id ->
            return map.getFunctionLabels(id)
        }, decoderId)
        return retVal
    }

    LabelVersion getLabelVersionMaxFor(int decoderId) {
        log.debug("getting the max label version (if any) for decoder id ${decoderId}")
        LabelVersion retVal = (LabelVersion)executeSql({ImportMapper map, int id ->
            return map.getLabelVersionMaxFor(id)
        }, decoderId)
        return retVal
    }

    SavedLabel writeSavedLabel(SavedLabel newLabel) {
        executeWrite {ImportMapper map ->
            map.insertSavedLabel(newLabel)
        }
        return newLabel
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
