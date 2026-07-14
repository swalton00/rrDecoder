package com.spw.rr.utilities

import com.spw.rr.database.*
import com.spw.rr.database.VersionBase.WhichTable
import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp

@Singleton
class DatabaseServices {

    private static final String SCHEMA_TEST = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?"
    private static final String CREATE_SCHEMA = "CREATE SCHEMA "
    private static final String TABLE_TEST =
            "SELECT COUNT(*) from information_schema.TABLES where table_schema = ? AND  TABLE_NAME = 'DB_VERSION'"
    private static final Logger log = LoggerFactory.getLogger(DatabaseServices.class)
    static final String SET_SCHEMA = "SET SCHEMA "
    static final String DB_VERSION = "SELECT major, minor, table_count FROM DB_VERSION where id = 1"
    private static final String RESOURCE_NAME = "createTables.sql"
    private static final String UPDATE_NAME_FRONT = "updateVersion_"
    private static final String MYBATIS_RESOURCE = "mybatis.xml"
    private static final Integer DB_MAJOR = 1
    private static final Integer DB_MINOR = 3

    SqlSessionFactory sqlSessionFactory
    SqlSession session

    String errorMessage
    boolean errorFound = false


    boolean validate(Settings settings) {
        validate(settings.userid, settings.password, settings.schema, settings.url)
    }

    String getErrorMessage() {
        log.debug("getting the last error message")
        if (!errorFound) {
            log.info("last error was requested but no previous error - returning empty string")
            return ""
        }
        errorFound = false
        return errorMessage
    }

    boolean validate(String userid, String password, String schema, String url) {
        log.debug("validating for  url: ${url}, Schema: ${schema}, userid: ${userid}")
        boolean returnValue = false
        String tempURL = url
        if (tempURL.contains(";SCHEMA=")) {
            log.error("URL should not contain SCHEMA")
            errorFound = true
            errorMessage = "URL Should not contain Schema"
            return false
        }
        log.debug("using a schema of ${schema} in the validator")
        log.debug("Testing database connection")
        Connection conn = null
        try {
            conn = DriverManager.getConnection(url, userid, password)
            if (conn != null) {
                log.debug("connection succeeded")
                PreparedStatement stmt = conn.prepareStatement(SCHEMA_TEST)
                stmt.setString(1, schema.toUpperCase())
                ResultSet rs = stmt.executeQuery()
                if (!rs.next()) {
                    log.error("result set didn't return any values")
                    throw new RuntimeException("ResultSet didn't give expected results")
                }
                int matchCount = rs.getInt(1)
                if (matchCount == 0) {
                    log.debug("schema not present -- creating")
                    PreparedStatement createSchema = conn.prepareStatement(CREATE_SCHEMA + schema)
                    createSchema.execute()
                }
                PreparedStatement stmt3 = conn.prepareStatement(TABLE_TEST)
                stmt3.setString(1, schema.toUpperCase())
                ResultSet rs2 = stmt3.executeQuery()
                if (!rs2.next()) {
                    log.error("search for tables didn't return a result set as it should")
                    throw new RuntimeException("Execute query to table count didn't return a result set")
                }
                matchCount = rs2.getInt(1)
                //  add checks for rest of tables here
                PreparedStatement stmt2 = conn.prepareStatement(SET_SCHEMA + schema)
                stmt2.execute()
                if (matchCount == 1) {
                    log.debug("tables found - checking db version")
                    PreparedStatement stmt4 = conn.prepareStatement(DB_VERSION)
                    ResultSet rs3 = stmt4.executeQuery()
                    if (!rs3.next()) {
                        log.error("search for DB Version didn't return a result set")
                        throw new RuntimeException("Execute query did not return a result set")
                    }
                    int majorVersion = rs3.getInt(1)
                    int minorVersion = rs3.getInt(2)
                    if (majorVersion != DB_MAJOR | minorVersion != DB_MINOR) {
                        log.error("mismatch on minor and major versions - Major = ${majorVersion} Minor = ${minorVersion}")
                        if (majorVersion != DB_MAJOR) {
                            log.error("Mismatch on database major version - should be ${DB_MAJOR} but is ${minorVersion}")
                            throw new RuntimeException("Mismatch on database major version - ${majorVersion} but should be ${DB_MAJOR}")
                        } else {
                            // run changes to update from minor version to current version
                            // changes will be on resource saved as  "update.vNN.sql"
                            // apply changes and reread minor version until minor version matches DB_MINOR
                            String resourceName = UPDATE_NAME_FRONT + majorVersion.toString() + "_" + minorVersion.toString() + ".sql"
                            log.info("Applying changes to update from ${minorVersion} from resource named ${resourceName}")
                            new ApplyResources().apply(resourceName, (Connection) conn)
                        }

                    }
                } else if (matchCount == 0) {
                    log.debug("DB version not found - creating tables")
                    new ApplyResources().apply(RESOURCE_NAME, (Connection) conn)

                }
                returnValue = true
            }
        } catch (Exception e) {
            log.error("exception working with the database", e)
            errorMessage = e.getMessage()
            errorFound = true
            //  throw new RuntimeException("exception working with the database")
        } finally {
            if (conn != null) {
                log.debug("closing connection")
                conn.close()
            }
        }
        return returnValue

    }

    void dbStart(Settings settings) {
        log.debug("starting the datasouce with settings ${settings}")
        Properties dbProps = new Properties()
        String newURL
        if (!settings.url.endsWith(";")) {
            newURL = settings.url + ";"
        } else {
            newURL = settings.url
        }
        newURL = newURL + "SCHEMA=" + settings.schema
        log.debug("Mybatis will be using URL of ${newURL}")
        dbProps.put("url", newURL)
        dbProps.put("username", settings.userid)
        dbProps.put("password", settings.password)
        dbProps.put("driver", "org.h2.Driver")
        InputStream inputStream = Resources.getResourceAsStream(MYBATIS_RESOURCE)
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, dbProps)
        settings.databaseOpen = true
    }

    void close() {
        log.debug("closing the session from the transaction")
        if (session == null) {
            throw new RuntimeException("attempting to run transInsertFunctionLabels outside of a transaction")
        }
        session.close()
        session = null
    }

    List<RosterEntry> listRosters() {
        log.debug("getting a list of the rosters")
        SqlSession session = sqlSessionFactory.openSession(true)
        Mapper map = session.getMapper(Mapper.class)
        List<RosterEntry> entries = map.listRosters()
        log.debug("got a list of size ${entries.size()}")
        session.close()
        return entries
    }

    List<DecoderType> listDecoderTypes() {
        log.debug("listing the decoder types in the database")
        SqlSession session = sqlSessionFactory.openSession(true)
        Mapper map = session.getMapper(Mapper.class)
        List<DecoderType> typeList = map.listDecoderTypes()
        log.debug("got a list of size ${typeList.size()}")
        session.close()
        return typeList
    }

    DecoderType insertDecoderTypeEntry(DecoderType type) {
        log.debug("inserting a new decoder type - ${type}")
        SqlSession mySession
        try {
            mySession = sqlSessionFactory.openSession(true)
            Mapper map = mySession.getMapper(Mapper.class)
            map.insertDecoderTypeEntry(type)
        } finally {
            mySession.close()
        }
        log.debug("result was ${type}")
        return type
    }

    void beginTransaction() {
        log.debug("starting a new Transaction")
        if (session != null) {
            log.error("Opening a new transaction when already in a transaction")
        }
        session = sqlSessionFactory.openSession(false)
    }

    RosterEntry getRosterEntry(String systemName, String fullPath) {
        log.debug("Retrieving roster for ${systemName} with path of ${fullPath}")
        SqlSession session
        RosterEntry result
        try {
            session = sqlSessionFactory.openSession(true)
            Mapper map = session.getMapper(Mapper.class)
            result = map.findRosterEntry(systemName, fullPath)
        } finally {
            session.close()
        }
        log.debug("result found was ${result}")
        return result
    }

    RosterEntry getRosterEntry(int rosterId) {
        log.debug("Retrieving roster with id of ${rosterId}")
        RosterEntry entry
        SqlSession session
        try {
            session = sqlSessionFactory.openSession(true)
            Mapper mapper = session.getMapper(Mapper.class)
            entry = mapper.getRosterEntryById(rosterId)
            log.debug("result found was ${entry}")
        } finally {
            session.close()
        }
        log.trace("returned entry is ${entry}")
        return entry
    }

    List<DecoderEntry> decodersForRosterList(List<Integer> rosters) {
        log.debug("Getting a list of decoders for ${rosters.size()}")
        SqlSession session
        List<DecoderEntry> results
        try {
            session = sqlSessionFactory.openSession(true)
            Mapper map = session.getMapper(Mapper.class)
            results = map.listDecodersFor(rosters)
        } finally {
            session.close()
        }
    }

    DecoderEntry getDecoderEntry(int id) {
        log.debug("Finding a decoder with id of ${id}")
        SqlSession session
        DecoderEntry decoder
        try {
            session = sqlSessionFactory.openSession(true)
            Mapper mapper = session.getMapper(Mapper.class)
            decoder = mapper.getDecoderEntry(id)
        } finally {
            session.close()
        }

        return decoder
    }

    DecoderEntry addDecoderEntry(DecoderEntry entry) {
        log.debug("adding a decoder entry ${entry}")
        if (session == null) {
            throw new RuntimeException("attempting to run addDecoderEntry outside of a transaction")
        }
        Mapper map = session.getMapper(Mapper.class)
        map.insertDecoderEntry(entry)
        log.debug("returning decoder with id of ${entry.id}")
        return entry
    }

    void updateDecoderEntry(DecoderEntry decoderEntry) {
        log.debug("updating decoder entry ${decoderEntry}")
        if (session == null) {
            throw new RuntimeException("attempting to run updateDecoderEntry outside of a transaction")
        }
        Mapper map = session.getMapper(Mapper.class)
        map.updateDecoderEntry(decoderEntry)
    }

    FunctionLabel insertFunctionLabel(FunctionLabel newValue, boolean useUpdate = false) {
        log.debug("inserting new FunctionLabel as part of a transaction - ${newValue}")
        if (session == null) {
            throw new RuntimeException("attempting to run insertFunctionLabels outside of a transaction")
        }
        Mapper map = session.getMapper(Mapper.class)
        if (useUpdate) {
            int updateCount = map.updateFunctionLabel(newValue)
            if (updateCount != 1) {
                log.error("Update count for ${newValue} expected to be 1, was actually #{updateCount}")
            }
        } else {
            map.insertFunctionLabel(newValue)
        }
        log.debug("returning result: ${newValue}")
        return newValue
    }

    void insertVersion(VersionBase version) {
        log.debug("inserting ${version}")
        if (session == null) {
            throw new RuntimeException("attempting to run insertVersion outside of a transaction")
        }
        Mapper map = session.getMapper(Mapper.class)
        String tableName = ""
        switch (version.tableSource) {
            case WhichTable.LABEL:
                tableName = "LABEL_VERSIONS"
                break
            case WhichTable.KEYVALUE:
                tableName = "KEYVALUES_VERSIONS"
                break
            case WhichTable.CV:
                tableName = "CV_VERSIONS"
                break
            default:
                log.error("VersionBase table source not set")
                throw new RuntimeException("Unknown value for table source ${version} and ${version.tableSource}")
        }
        map.insertVersion(version, tableName)
        version.hasBeenWritten = true
    }

    void deleteOldItems(WhichTable table, Integer decoderId, ArrayList<String> itemList) {
        log.debug("deleting old items from ${table}, decoder was ${decoderId} and item list ${itemList}")
        if (session == null) {
            throw new RuntimeException("attempting to run insertVersion outside of a transaction")
        }
        Mapper map = session.getMapper(Mapper.class)
        String tableName = ""
        String columnName = ""
        switch (table) {
            case VersionBase.WhichTable.LABEL:
                tableName = "FUNCTIONLABELS"
                columnName = "FUNCTIONNUMBER"
                break
            case VersionBase.WhichTable.KEYVALUE:
                tableName = "KEYVALUES"
                columnName = "PAIR_KEY"
                break
            case VersionBase.WhichTable.CV:
                tableName = "CVVALUES"
                columnName = "CVNUMBER"
                break
            default:
                thrown new RuntimeException("Unknown value for table source ${version} and ${version.tableSource}")
        }
        int itemCount = map.deleteObsoleteItems(tableName, columnName, decoderId, itemList)
        if (itemCount != itemList.size()) {
            throw new RuntimeException("Delete count doesn't match expected - expected ${itemList.size()} and was ${itemCount}")
        }
    }

    void insertLabelDiff(LabelDiff labelDiff) {
        log.debug("inserting new LabelDiff ${labelDiff}")
        if (session == null) {
            throw new RuntimeException("attempting to insert LabelDiff outside of a transaction")
        }
        Mapper map = session.getMapper(Mapper.class)
        map.insertLabelDiff(labelDiff)
    }

    void insertKeyDiff(KeyDiff diff) {
        log.debug("inserting new KeyDiff ${diff}")
        if (session == null) {
            throw new RuntimeException("attempting to insert LabelDiff outside of a transaction")
        }
        Mapper map = session.getMapper(Mapper.class)
        map.insertKeyValueDiff(diff)
    }

    void insertDiff(AbstractDiff newDiff, WhichTable tableType) {
        log.debug("inserting a new Diff - ${newDiff}")
        if (session == null) {
            throw new RuntimeException("attempting to insert AbstractDiff outside of a transaction")
        }
        Mapper map = session.getMapper(Mapper.class)
        switch (tableType) {
            case WhichTable.LABEL:
                map.insertLabelDiff((LabelDiff) newDiff)
                break
            case WhichTable.KEYVALUE:
                map.insertKeyValueDiff((KeyDiff) newDiff)
                break
            case WhichTable.CV:
                map.insertCVDiff((CV_Diff) newDiff)
                break
            default:
                log.error("unknown Diff type requested")
        }
    }

    KeyValuePairs insertKeyValuePair(KeyValuePairs kvp, boolean useUpdate = false) {
        log.debug("adding a new KeyValuePair: ${kvp}")
        if (session == null) {
            throw new RuntimeException("attempting to insert a new KeyValuePair outside a transaction")
        }
        Mapper map = session.getMapper(Mapper.class)
        if (useUpdate) {
            int updatedCount = map.updateKeyValuePairs(kvp)
            if (updatedCount != 1) {
                log.error("update count was not equal to 1 - was ${updatedCount}")
            }
        } else {
            map.insertKeyValuePairs(kvp)
        }
        log.debug("inserted value was ${kvp}")
        return kvp
    }

    SpeedProfile insertSpeedProfile(SpeedProfile sp) {
        log.debug("adding a new SpeedProfile: ${sp}")
        if (session == null) {
            throw new RuntimeException("attempting to add a speed profile outside a transaction")
        }
        Mapper mapper = session.getMapper(Mapper.class)
        mapper.insertSpeedProfile(sp)
        log.debug("inserted value was ${sp}")
        return sp
    }

    CvValues insertCVs(CvValues cVvalues, boolean usUpdate) {
        log.debug("adding new CV value: ${cVvalues} for a transaction")
        if (session == null) {
            throw new RuntimeException("attempting to insert a new CV outside a transaction")
        }
        Mapper mapper = session.getMapper(Mapper.class)
        if (usUpdate) {
            int updateCount = mapper.updateCVs(cVvalues)
            if (updateCount != 1) {
                log.error("update count should be 1 - was ${updateCount}")
                throw new RuntimeException("Update count should be 1 - was ${updateCount}")
            }
        } else {
            mapper.insertCVs(cVvalues)
        }
        log.debug("result was ${cVvalues}")
        return cVvalues
    }

    int updateDetailTime(Integer decoderId, Timestamp timestamp) {
        log.debug("update detail timestamp for decoder id ${decoderId} in a transaction")
        if (session == null) {
            throw new RuntimeException("attempting to insert a new KeyValuePair outside a transaction")
        }
        Mapper mapper = session.getMapper(Mapper.class)
        return mapper.updateDecoderDetailTime(decoderId, timestamp)
    }

    int deleteDecoderEntry(DecoderEntry decoderEntry) {
        log.debug("Deleting decoder with id of ${decoderEntry.id} within a transaction")
        if (session == null) {
            throw new RuntimeException("transaction but current session is null")
        }
        Mapper mapper = session.getMapper(Mapper.class)
        int result = mapper.deleteDecoderEntry(decoderEntry)
        log.debug("return result of ${result}")
        return result
    }

    void commitWork() {
        log.debug("committing the transaction")
        if (session == null) {
            throw new RuntimeException("Attempting to commit work when not in a transaction")
        }
        session.commit()
        session.close()
        session = null
    }

    void rollbackAll() {
        if (session == null) {
            throw new RuntimeException("Attempting to rollback work when not in a transaction")
        }
        session.rollback()
        session.close()
        session = null
    }


    VersionBase getLastVersion(Integer decoderId, VersionBase.WhichTable sourceTable) {
        log.debug("getting the last version record for ${sourceTable}")
        String tableName = ""
        SqlSession session
        boolean inTransaction = false
        switch (sourceTable) {
            case VersionBase.WhichTable.CV:
                tableName = "CV_VERSIONS"
                break
            case VersionBase.WhichTable.LABEL:
                tableName = "LABEL_VERSIONS"
                break
            case VersionBase.WhichTable.KEYVALUE:
                tableName = "KEYVALUES_VERSIONS"
                break
            default:
                log.error("incorrect table passed to getLastVersion - ${sourceTable}")
        }
        VersionBase version = null
        try {
            if (this.session == null) {
                session = sqlSessionFactory.openSession(true)
            } else {
                session = this.session
                inTransaction = true
            }
            Mapper map = session.getMapper(Mapper.class)
            version = map.getLastVersion(decoderId, tableName)
            log.debug("version number returned is ${version}")
        } catch (Exception e) {
            log.error("Exception attempting to get the last version", e)
        } finally {
            if (!inTransaction) {
                session.close()
            }
            if (version) {
                version.tableSource = sourceTable
            }
            return version
        }
    }

    ArrayList<AbstractItem> getItemsFors(int decoderId, WhichTable tableType) {
        ArrayList<AbstractItem> returnList = null
        try {
            SqlSession session = setup()
            Mapper map = session.getMapper(Mapper.class)
            switch (tableType) {
                case WhichTable.LABEL:
                    returnList = map.getFunctionLabels(decoderId)
                    break
                case WhichTable.KEYVALUE:
                    returnList = map.getKeyValuesFor(decoderId)
                    break
                case WhichTable.CV:
                    returnList = map.getCvValuesFor(decoderId)
                    break
                default:
                    log.error("incorrect selection of tableType - value ${tableType}")
                    throw new RuntimeException("Incorrect Table Type ${tableType}")
            }
        } catch (Exception e) {
            log.error("Exception running sql for ${tableType}", e)
        } finally {
            tearDown(session)
        }
        return returnList
    }

    SqlSession setup() {
        SqlSession returnSession
        if (this.session == null) {
            returnSession = sqlSessionFactory.openSession(true)
        } else {
            returnSession = this.session
        }
        return returnSession
    }

    void tearDown(SqlSession currentSession) {
        if (this.session == null) {
            currentSession.close()
        }
    }


}
