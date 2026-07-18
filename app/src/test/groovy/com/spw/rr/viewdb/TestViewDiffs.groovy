package com.spw.rr.viewdb

import com.spw.rr.database.DecoderEntry
import com.spw.rr.utilities.ApplyResources
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
import static org.junit.jupiter.api.Assertions.assertTrue

import java.sql.Timestamp

@Slf4j
class TestViewDiffs {

    static DatabaseServices database = DatabaseServices.getInstance()
    static ViewDbService viewDb = ViewDbService.getInstance()

    @BeforeAll static void setupTests() {
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
        ArrayList<DecoderEntry> entries = viewDb.getDecoderDiffs(ViewDb.SelectType.SELECT_ALL_CVS,
                ViewDb.DiffType.ALL_VALUES,
                getList)
        log.debug("returned list was ${entries}")
        assertEquals(3, entries.size())
    }

    @Test
    void testKnownGood() {
        log.debug("getting values from a known good routine")
        ArrayList<DecoderEntry> entries =  viewDb.listDecoderByRosterId([1, 2, 3])
        log.debug("got the list - it is ${entries}")
        assertEquals(7, entries.size())
    }
}
