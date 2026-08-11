package com.spw.rr.viewdb

import com.spw.rr.database.AbstractDiff
import com.spw.rr.database.AbstractItem
import com.spw.rr.database.CV_Diff
import com.spw.rr.database.CvValues
import com.spw.rr.database.DecoderEntry
import com.spw.rr.database.FunctionLabel
import com.spw.rr.database.VersionBase
import com.spw.rr.utilities.ApplyResources
import com.spw.rr.utilities.BuildKeyList
import com.spw.rr.utilities.DatabaseServices
import com.spw.rr.utilities.Settings
import groovy.util.logging.Log4j
import groovy.util.logging.Slf4j
import org.apache.ibatis.session.SqlSession
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import java.sql.Array
import java.sql.Blob
import java.sql.Clob
import java.sql.Connection
import java.sql.Date
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.Ref
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException
import java.sql.Time

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertTrue

import java.sql.Timestamp

@Slf4j
class TestViewDiffs {

    static DatabaseServices database = DatabaseServices.getInstance()
    static ViewDbService viewDb = ViewDbService.getInstance()

    @BeforeAll
    static void setupTests() {
        String url = "jdbc:h2:mem:memory;DB_CLOSE_DELAY=-1"
        Settings testSettings = new Settings()
        testSettings.password = "testpw"
        testSettings.userid = "test"
        testSettings.schema = "test"
        testSettings.url = url
        log.debug("ready to validate database")
        boolean validateResults = database.validate(testSettings)
        log.debug("validation results are ${validateResults}")
        log.debug("adding test data")
        database.dbStart(testSettings)
        SqlSession session = database.sqlSessionFactory.openSession()
        Connection conn = session.getConnection()
        PreparedStatement stmt = conn.prepareStatement("Set Schema test")
        stmt.execute()

        ApplyResources applyResources = new ApplyResources()
        applyResources.apply("CV_TestData.sql", conn)
        log.debug("now ready for tests")


        PreparedStatement stmt2 = conn.prepareStatement("Select count(*) from decoder")
        ResultSet rs = stmt2.executeQuery()
        if (rs.next()) {
            log.debug("in the next - value is ${rs.getInt(1)}")
        } else {
            log.debug("empty result set")
        }
        conn.commit()
        conn.close()
    }

    @Test
    void testMybatisDiffView() {
        Timestamp dbTime = database.getCurrentDbTime()
        assertTrue(dbTime != null)
    }

    @Test
    void testRetrieval() {
        def getList = [143, 144, 145]
        log.debug("retrieving decoder diffs for ${getList}")
        ArrayList<DecoderEntry> entries = viewDb.getDecDiffs(ViewDb.SelectType.SELECT_ALL_CVS,
                ViewDb.DiffType.ALL_VALUES,
                getList)
        /*
           don't log here - it will trigger the proxies
         */
        assertEquals(3, entries.size())
        log.debug("about to reference cvvalues in entries")
        log.debug("values in entry 0 are ${entries.get(0).cvVersions} ${entries.get(0).keyVersions} ${entries.get(0).labelVersions}")
        assertEquals(3, entries.size())
        log.debug("entries #2 (145) is ${entries.get(2)}")
        assertNotNull(entries.get(0).values)
    }

    @Test
    void testDiffOnlyCV() {
        def getList = [143, 144, 145]
        log.debug("retrieving decoder diffs for ${getList}")
        ArrayList<DecoderEntry> entries = viewDb.getDecDiffs(ViewDb.SelectType.SELECT_ALL_CVS,
                ViewDb.DiffType.ONLY_CHANGED,
                getList)
        /*
           don't log here - it will trigger the proxies
         */
        assertEquals(3, entries.size())
        log.debug("entry size is ${entries.size()}")
        log.debug("versions size for 0 is ${entries.get(0).versions.size()}")
        log.debug("first diff size is ${entries.get(0).versions.get(0).diffList.size()}")
    }

    @Test
    void testDiffKeys() {
        def getList = [143, 144, 145]
        log.debug("retrieving decoder diffs for ${getList}")
        ArrayList<DecoderEntry> entries = viewDb.getDecDiffs(ViewDb.SelectType.SELECT_KEY,
                ViewDb.DiffType.ALL_VALUES,
                getList)
        /*
           don't log here - it will trigger the proxies
         */
        assertEquals(3, entries.size())
        log.debug("entry size is ${entries.size()}")
        log.debug("versions size for 0 is ${entries.get(0).versions.size()}")
        if (entries.get(0).versions.size() > 0) {
            log.debug("first diff size is ${entries.get(0).versions.get(0).diffList.size()}")
        }
    }


    @Test
    void testDiffKeysOnly() {
        def getList = [143, 144, 145]
        log.debug("retrieving decoder diffs for ${getList}")
        ArrayList<DecoderEntry> entries = viewDb.getDecDiffs(ViewDb.SelectType.SELECT_KEY,
                ViewDb.DiffType.ONLY_CHANGED,
                getList)
        /*
           don't log here - it will trigger the proxies
         */
        assertEquals(3, entries.size())
        log.debug("entry size is ${entries.size()}")
        log.debug("versions size for 0 is ${entries.get(0).versions.size()}")
        if (entries.get(0).versions.size() > 0) {
            log.debug("first diff size is ${entries.get(0).versions.get(0).diffList.size()}")
        }
    }


    @Test
    void testDiffFunctions() {
        def getList = [143, 144, 145]
        log.debug("retrieving decoder diffs for ${getList}")
        ArrayList<DecoderEntry> entries = viewDb.getDecDiffs(ViewDb.SelectType.SELECT_FUNC,
                ViewDb.DiffType.ALL_VALUES,
                getList)
        /*
           don't log here - it will trigger the proxies
         */
        assertEquals(3, entries.size())
        log.debug("entry size is ${entries.size()}")
        log.debug("versions size for 0 is ${entries.get(0).versions.size()}")
        log.debug("first diff size is ${entries.get(0).versions.get(0).diffList.size()}")
    }


    @Test
    void testDiffFuncONly() {
        def getList = [143, 144, 145]
        log.debug("retrieving decoder diffs for ${getList}")
        ArrayList<DecoderEntry> entries = viewDb.getDecDiffs(ViewDb.SelectType.SELECT_FUNC,
                ViewDb.DiffType.ONLY_CHANGED,
                getList)
        /*
           don't log here - it will trigger the proxies
         */
        assertEquals(3, entries.size())
        log.debug("entry size is ${entries.size()}")
        log.debug("versions size for 0 is ${entries.get(0).versions.size()}")
        log.debug("first diff size is ${entries.get(0).versions.get(0).diffList.size()}")
    }


    @Test
    void testBuildKeys() {
        log.debug("getting values to test buildKeys")
        def getList = [143, 144, 145]
        ArrayList<DecoderEntry> entries = viewDb.getDecDiffs(ViewDb.SelectType.SELECT_ALL_CVS,
                ViewDb.DiffType.ALL_VALUES,
                getList)
        log.debug("got the list - it is ${entries}")
        assertEquals(3, entries.size())
        List<String> keyList = BuildKeyList.buildList(entries,
                null,
                { DecoderEntry dec ->
                    if (!(dec instanceof DecoderEntry)) {
                        log.error("wrong type for parameter, ${dec}")
                    }
                    log.debug("returning dec.versions ${dec.versions}")
                    return dec.values
                },
                null,
                { AbstractItem thisItem ->
                    if (!(thisItem instanceof AbstractItem)) {
                        log.debug("wrong type - should be AbstractItem for ${thisDiff}")
                    } else {
                        return thisItem.key
                    }
                },
                null,
                false,
                null,
                null)
        log.debug("keyList built is ${keyList}")
        assertNotNull(keyList)
    }

    @Test
    void testBuildSubKeys() {
        log.debug("getting values to test buildKeys")
        def getList = [143, 144, 145]
        ArrayList<DecoderEntry> entries = viewDb.getDecDiffs(ViewDb.SelectType.SELECT_ALL_CVS,
                ViewDb.DiffType.ALL_VALUES,
                getList)
        log.debug("got the list - it is ${entries}")
        assertEquals(3, entries.size())
        List<String> keyList = BuildKeyList.buildList(entries,
                null,
                { DecoderEntry dec ->
                    if (!(dec instanceof DecoderEntry)) {
                        log.error("wrong type for parameter, ${dec}")
                    }
                    log.debug("returning dec.versions ${dec.versions}")
                    return dec.versions
                },
                { VersionBase version ->
                    if (!(version in VersionBase)) {
                        log.error("wrong sub type")
                    }
                    return version.diffList
                },
                { AbstractDiff thisDiff ->
                    if (!(thisDiff instanceof AbstractDiff)) {
                        log.debug("wrong type - should be CV_Diff for ${thisDiff}")
                    } else {
                        return thisDiff.key
                    }
                },
                null,
                false,
                BuildKeyList.getCVHash,
                BuildKeyList.putCVHash)
        log.debug("keyList built is ${keyList}")
        assertNotNull(keyList)
        assertNotNull(entries.get(0).versions)
        assertNotNull(entries.get(0).versions.get(0).keyValues)
    }


    @Test
    void testBuildKeysWithHash() {
        log.debug("getting values to test buildKeys")
        def getList = [143, 144, 145]
        ArrayList<DecoderEntry> entries = viewDb.getDecDiffs(ViewDb.SelectType.SELECT_ALL_CVS,
                ViewDb.DiffType.ALL_VALUES,
                getList)
        log.debug("got the list - it is ${entries}")
        assertEquals(3, entries.size())
        List<String> keyList = BuildKeyList.buildList(entries,
                null,
                { DecoderEntry dec ->
                    if (!(dec instanceof DecoderEntry)) {
                        log.error("wrong type for parameter, ${dec}")
                    }
                    log.debug("returning dec.versions ${dec.versions}")
                    return dec.values
                },
                null,
                { AbstractItem thisItem ->
                    if (!(thisItem instanceof AbstractItem)) {
                        log.debug("wrong type - should be AbstractItem for ${thisDiff}")
                    } else {
                        return thisItem.key
                    }
                },
                null,
                false,
                { ->
                    return new Hashtable<String, CvValues>()
                },
                { Object theItem, Hashtable<Object, Object> theHash ->
                    if (!(theHash instanceof Hashtable<String, CvValues>)) {
                        log.debug("the hashtable is the wrong type ${theHash}")
                    }
                    if (!(theItem instanceof CvValues)) {
                        log.debug("theItem is the wrong type ${theItem}")
                    }
                    theHash.put(theItem.key, theItem)
                })
        log.debug("keyList built is ${keyList}")
        assertNotNull(keyList)
        assertNotNull(entries.size())
    }

    @Test
    void testBuildLabelsWithHashAndSub() {
        log.debug("getting values to test buildKeys")
        def getList = [143, 144, 145]
        ArrayList<DecoderEntry> entries = viewDb.getDecDiffs(ViewDb.SelectType.SELECT_FUNC,
                ViewDb.DiffType.ALL_VALUES,
                getList)
        log.debug("got the list - it is ${entries}")
        assertEquals(3, entries.size())
        List<String> keyList = BuildKeyList.buildList(entries,
                null,
                { DecoderEntry dec ->
                    if (!(dec instanceof DecoderEntry)) {
                        log.error("wrong type for parameter, ${dec}")
                    }
                    log.debug("returning dec.versions ${dec.versions}")
                    return dec.values
                },
                null,
                { AbstractItem thisItem ->
                    if (!(thisItem instanceof AbstractItem)) {
                        log.debug("wrong type - should be AbstractItem for ${thisDiff}")
                    } else {
                        return thisItem.key
                    }
                },
                null,
                false,
                { ->
                    return new Hashtable<String, CvValues>()
                },
                { Object theItem, Hashtable<Object, Object> theHash ->
                    if (!(theHash instanceof Hashtable<String, CvValues>)) {
                        log.debug("the hashtable is the wrong type ${theHash}")
                    }
                    if (!(theItem instanceof CvValues)) {
                        log.debug("theItem is the wrong type ${theItem}")
                    }
                    theHash.put(theItem.key, theItem)
                })
        keyList = BuildKeyList.buildList(entries,
                keyList,
                { DecoderEntry dec ->
                    if (!(dec instanceof DecoderEntry)) {
                        log.error("wrong type for parameter, ${dec}")
                    }
                    log.debug("returning dec.versions ${dec.values}")
                    return dec.values
                },
                null,
                { AbstractItem thisItem ->
                    if (!(thisItem instanceof AbstractItem)) {
                        log.debug("wrong type - should be AbstractItem for ${thisDiff}")
                    } else {
                        return thisItem.key
                    }
                },
                null,
                true,
                { ->
                    return new Hashtable<String, FunctionLabel>()
                },
                { Object theItem, Hashtable<Object, Object> theHash ->
                    if (!(theHash instanceof Hashtable<String, CvValues>)) {
                        log.debug("the hashtable is the wrong type ${theHash}")
                    }
                    if (!(theItem instanceof FunctionLabel)) {
                        log.debug("theItem is the wrong type ${theItem}")
                    }
                    theHash.put(theItem.key, theItem)
                })
        log.debug("keyList built is ${keyList}")
        assertNotNull(keyList)
        assertNotNull(entries.keyValues)
        assertNotNull(entries.versions.get(0).keyValues)
    }

}
