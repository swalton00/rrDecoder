package com.spw.rr.utilities

import com.spw.rr.database.*
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
    private static final Integer DB_MINOR = 2

    SqlSessionFactory sqlSessionFactory
    SqlSession session


    boolean validate(Settings settings) {
        log.debug("validating for  $settings}")
        String schema
        boolean returnValue = false
        /*
        if url contains schema, remove schema first to validate (schema might not be present in database
        then, connect, check for schemaa (if not present,
        finally check for tables
         */
        String tempURL = settings.url
        if (tempURL.contains(";SCHEMA=")) {
            int schemaStart = tempURL.indexOf(";SCHEMA=")
            int schemaEnd = tempURL.indexOf(";", schemaStart + 1)
            schema = tempURL.substring(schemaStart + ";SCHEMA=".size())
            if (schemaEnd == -1) {
                tempURL = tempURL.substring(0, schemaStart - 1)
                log.trace("URL without the schema is ${tempURL}")
            } else {
                String front = tempURL.substring(0, schemaStart)
                String back = tempURL.substring(schemaEnd)
                tempURL = front + back
                log.trace("URL schema removed is ${tempURL}")
            }
        } else if (settings.schema == null) {
            log.error("no schema passed")
        } else {
            schema = settings.schema
        }
        log.debug("using a schema of ${schema} in the validator")
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
                stmt3.setString(1, settings.schema.toUpperCase())
                ResultSet rs2 = stmt3.executeQuery()
                if (!rs2.next()) {
                    log.error("search for tables didn't return a result set as it should")
                    throw new RuntimeException("Execute query to table count didn't return a result set")
                }
                matchCount = rs2.getInt(1)
        //  add checks for rest of tables here
                PreparedStatement stmt2 = conn.prepareStatement(SET_SCHEMA + settings.schema)
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
                    if (majorVersion != DB_MAJOR | minorVersion != DB_MINOR)
                    {
                        log.error("mismatch on minor and major versions - Major = ${majorVersion} Minor = ${minorVersion}")
                        if (majorVersion != DB_MAJOR) {
                            log.error("Mismatch on database major version - should be ${DB_MAJOR} but is ${minorVersion}")
                            throw new RuntimeException("Mismatch on database major version - ${majorVersion} but should be ${DB_MAJOR}")
                        } else {
                            // run changes to update from minor version to current version
                            // changes will be on resource saved as  "update.vNN.sql"
                            // apply changes and reread minor version until minor version matches DB_MINOR
                            String resourceName = UPDATE_NAME_FRONT + majorVersion.toString() + "_"  + minorVersion.toString() + ".sql"
                            log.info("Applying changes to update from ${minorVersion} from resource named ${resourceName}")
                            new ApplyResources().apply(resourceName, (Connection) conn)
                        }

                    }
                } else if (matchCount == 0) {
                    log.debug("DB version not found - creating tables")
                    new ApplyResources().apply(RESOURCE_NAME, (Connection) conn)

                }
                returnValue = true
                settings.settingsValid = true
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
        return returnValue

    }

    void dbStart(Settings settings) {
        log.debug("starting the datasouce with settings ${settings}")
        Properties dbProps = new Properties()
        dbProps.put("url", settings.url)
        dbProps.put("username", settings.userid)
        dbProps.put("password", settings.password)
        dbProps.put("driver", "org.h2.Driver")
        InputStream inputStream = Resources.getResourceAsStream(MYBATIS_RESOURCE)
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, dbProps)
        settings.databaseOpen = true
    }

    void dbClose() {
        log.debug("closing the Mybatis database")
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
        boolean sessionOpened
        SqlSession mySession
        if (session != null) {
            log.debug("using an existing session for insert Decoder type entry")
            mySession = session
        } else {
            mySession = sqlSessionFactory.openSession(true)
            sessionOpened = true
        }
        Mapper map = mySession.getMapper(Mapper.class)
        map.insertDecoderTypeEntry(type)
        if (sessionOpened) session.close()
        log.debug("result was ${type}")
        return type
    }

    void beginTransaction() {
        log.debug("starting a new Transaction")
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
        } catch (Exception e) {
            log.error("Exception process the SQL", e)
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
        } catch (Exception e) {
            log.error("Exception process the SQL", e)
        } finally {
            session.close()
        }
        log.trace("returned entry is ${entry}")
        return entry
    }


    void prepareDetail(Integer decoderId) {
        log.debug("preparing for Detail import by deleting CvValue and DecoderDef where decoderId = ${decoderId}")
        if (session == null) {
            throw new RuntimeException("attempting to run decoderss for roster outside of a transaction")
        }
        Mapper mapper = session.getMapper(Mapper.class)
        int rowsDeleted = mapper.deleteCVs(decoderId)
        log.debug("CVvalues rows deleted = ${rowsDeleted}")
        rowsDeleted = mapper.deleteDecoderDef(decoderId)
        log.debug("DecoderDef rows deleted = ${rowsDeleted}")
    }

    List<DecoderEntry> decodersForRoster(int rosterID) {
        log.debug("listing decoders for rosterID ${rosterID}")
        if (session == null) {
            throw new RuntimeException("attempting to run decoderss for roster outside of a transaction")
        }
        Mapper map = session.getMapper(Mapper.class)
        List<DecoderEntry> result = map.listDecodersByRosterID(rosterID)
        log.debug("result was ${result}")
        return result
    }

    List<DecoderEntry> decodersForRosterList(List<Integer> rosters) {
        log.debug("Getting a list of decoders for ${rosters.size()}")
        SqlSession session
        List<DecoderEntry> results
        try {
            session = sqlSessionFactory.openSession(true)
            Mapper map = session.getMapper(Mapper.class)
            results = map.listDecodersFor(rosters)
        } catch (Exception e) {
            log.error("Exception process the SQL", e)
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
        } catch (Exception e) {
            log.error("Exception process the SQL", e)
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

    FunctionLabel insertFunctionLabel(FunctionLabel newValue) {
        log.debug("inserting new FunctionLabel as part of a transaction - ${newValue}")
        if (session == null) {
            throw new RuntimeException("attempting to run insertFunctionLabels outside of a transaction")
        }
        Mapper map = session.getMapper(Mapper.class)
        map.insertFunctionLabel(newValue)
        log.debug("returning result: ${newValue}")
        return newValue
    }

    KeyValuePairs insertKeyValuePair(KeyValuePairs kvp) {
        log.debug("adding a new KeyValuePair: ${kvp}")
        if (session == null) {
            throw new RuntimeException("attempting to insert a new KeyValuePair outside a transaction")
        }
        Mapper map = session.getMapper(Mapper.class)
        map.insertKeyValuePairs(kvp)
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

    DecoderDef insertDecoderDef(DecoderDef decoderDef) {
        log.debug("adding new decoder defininiton: ${decoderDef} inside a transaction")
        if (session == null) {
            throw new RuntimeException("attempting to insert a new KeyValuePair outside a transaction")
        }
        Mapper mapper = session.getMapper(Mapper.class)
        mapper.insertDecoderDef(decoderDef)
        log.debug("result was ${decoderDef}")
        return decoderDef
    }


    CvValues insertCVs(CvValues cVvalues) {
        log.debug("adding new CV value: ${cVvalues} for a transaction")
        if (session == null) {
            throw new RuntimeException("attempting to insert a new KeyValuePair outside a transaction")
        }
        Mapper mapper = session.getMapper(Mapper.class)
        mapper.insertCVs(cVvalues)
        log.debug("result was ${cVvalues}")
        return cVvalues
    }

    int updateDetailTime(Integer decoderId) {
        log.debug("update detail timestamp for decoder id ${decoderId} in a transaction")
        if (session == null) {
            throw new RuntimeException("attempting to insert a new KeyValuePair outside a transaction")
        }
        Mapper mapper = session.getMapper(Mapper.class)
        return mapper.updateDecoderDetailTime(decoderId)
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
    }

    void rollbackAll() {
        if (session == null) {
            throw new RuntimeException("Attempting to rollback work when not in a transaction")
        }
        session.rollback()
    }

}
