package com.spw.rr

import com.spw.rr.database.DecoderEntry
import com.spw.rr.viewdb.ViewDb
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertNull

class TestDbService {

    static DbService database
    private static final Logger log = LoggerFactory.getLogger(TestDbService.class)

    @BeforeAll
    static void createDatabase() {
        log.debug("setting up resources")
        database = new DbService()
        database.dbStart()
        log.debug("database resources now initialized")
    }


    @Test
    void testWithFixed() {
        log.debug("testing fixed values")
        Vector<Integer> list = new Vector<>()
        list.addAll([7, 8, 9])
        List<DecoderEntry> returned = database.listWithFixed(list)
        log.debug("returned list size was ${returned.size()}")
        assertEquals(3, returned.size())
        assertEquals(6, returned.get(0).cvValues.size())
        log.debug("size of cvLists are: 0 -${returned.get(0).cvValues.size()}")
    }

    @Test
    void testWithParameters() {
        log.debug("testing with paramters")
        Vector<Integer> list = new Vector<>()
        list.addAll([7, 8, 9])
        ArrayList<String> cvNumbers = new ArrayList<>()
        cvNumbers.addAll(["1", "2", "3", "4", "5", "6"])
        List<DecoderEntry> returned = database.listWithParameters(list, cvNumbers)
        log.debug("returned list was ${returned}")
        assertEquals(3, returned.size())
        assertEquals(6, returned.get(0).cvValues.size())
        log.debug("cvalues returned are ${returned.get(0).cvValues.size()}")
    }

    @Test
    void testNewList() {
        log.debug("testing new list type")
        Vector<Integer> list = new Vector()
        list.addAll([7, 8, 9])
        List<DecoderEntry> returned = database.listValues(ViewDb.SelectType.SELECT_FXD_CVS, list, null)
        log.debug("returned ${returned.size()} rows")
        assertEquals(3, returned.size())
        assertNotNull(returned.cvValues)
        assertEquals(6, returned.cvValues.get(0).size())
    }

    @Test
    void testSelectTypeSelCvs() {
        log.debug("testing ViewDb.SelectType.SELECT_SEL_CVS")
        Vector<Integer> list = new Vector<>()
        list.addAll([7, 8, 9])
        ArrayList<String> cvList = new ArrayList<>()
        cvList.addAll("2", "5", "6", "230")
        List<DecoderEntry> returned = database.listValues(ViewDb.SelectType.SELECT_SEL_CVS, list, cvList)
        log.debug("Returned ${returned.size()} rows for SELECT_SEL_CVS")
        assertEquals(3, returned.size())
        assertNotNull(returned.get(0).cvValues)
        assertEquals(3, returned.cvValues.get(0).size())
    }

    @Test
    void testSelectTypeAllCvs() {
        log.debug("testing ViewDb.SelectType.SELECT_ALL_CVS with rows 7, 8, 9")
        Vector<Integer> list = new Vector<>()
        list.addAll([7, 8, 9])
        List<DecoderEntry> returned = database.listValues(ViewDb.SelectType.SELECT_ALL_CVS, list, null)
        log.debug("Returned ${returned.size()} rows for SELECT_ALL_CVS")
        assertEquals(3, returned.size())
        assertNotNull(returned.get(0).cvValues)
        assertEquals(6, returned.cvValues.get(0).size())
    }

    @Test
    void testSelectTypeSelectFunc() {
        log.debug("testing ViewDb.SelectType.SELECT_FUNC")
        Vector<Integer> list = new Vector<>()
        list.addAll([7, 8, 9])
        ArrayList<String> cvNumbers = new ArrayList<>()
        List<DecoderEntry> returned = database.listValues(ViewDb.SelectType.SELECT_FUNC, list, null)
        log.debug("Returned ${returned.size()} rows for SELECT_CUSTOM")
        assertEquals(3, returned.size())
        assertEquals(0, returned.get(0).cvValues.size())
        assertEquals(9, returned.get(0).labelValues.size())
        log.debug("label values returned are ${returned.get(0).labelValues}")
    }

    @Test
    void testSelectTypeSelectDEF() {
        log.debug("testing ViewDb.SelectType.SELECT_DEF")
        Vector<Integer> list = new Vector<>()
        list.addAll([7, 8, 9])
        ArrayList<String> cvNumbers = new ArrayList<>()
        List<DecoderEntry> returned = database.listValues(ViewDb.SelectType.SELECT_DEF, list, null)
        log.debug("Returned ${returned.size()} rows for Select Decoder Def")
        assertEquals(3, returned.size())
        assertEquals(0, returned.get(0).cvValues.size())
        assertEquals(0, returned.get(0).labelValues.size())
        assertEquals(9, returned.get(0).defValues.size())
        log.debug("def values returned are ${returned.get(0).defValues}")
    }

}
