package com.spw.rr

import com.spw.rr.database.DecoderEntry
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.junit.jupiter.api.Assertions.assertEquals

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

}
