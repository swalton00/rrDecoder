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

    VersionBase getLastVersion(Integer decoderId, VersionBase.WhichTable sourceTable ) {
        log.debug("getting the last version record for ${sourceTable}")
        String tableName = ""
        switch (sourceTable) {
            case VersionBase.WhichTable.CV :
                tableName = "CV_VERSIONS"
                break
            case VersionBase.WhichTable.LABEL :
                tableName = "LABEL_VERSIONS"
                break
            case VersionBase.WhichTable.KEYVALUE :
                tableName = "KEYVALUES_VERSIONS"
                break
            default:
                log.error("incorrect table passed to getLastVersion - ${sourceTable}")
        }
        SqlSession session
        VersionBase version = null
        try {
            session = parent.sqlSessionFactory.openSession(true)
            ImportMapper map = session.getMapper(ImportMapper.class)
            version = map.getLastVersion(decoderId, tableName)
            log.debug("version number returned is ${version}")
        } catch (Exception e) {
            log.error("Exception attempting to get the last version", e)
        } finally {
        return version
        }
    }

    ArrayList<FunctionLabel> getFunctionLabelsFor(int decoderId) {
        log.debug("getting a list of FunctionLabels for decoderId ${decoderId}")
        ArrayList<FunctionLabel> retVal  = (ArrayList<FunctionLabel>)executeSql({ImportMapper map, int id ->
            return map.getFunctionLabels(id)
        }, decoderId)
        return retVal
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
