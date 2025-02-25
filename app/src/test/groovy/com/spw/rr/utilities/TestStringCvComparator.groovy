package com.spw.rr.utilities

import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.junit.jupiter.api.Assertions.assertEquals

class TestStringCvComparator {

    private static final Logger log = LoggerFactory.getLogger(TestStringCvComparator.class)

    @Test
    void testBasicComparator() {
        log.debug("testing single cv numbers")
        CvNameComparator comp = new StringCvComparator()
        String left = "3"
        String right = "2"
        int result = comp.compare(left, right)
        log.debug("result of comparing ${left} with ${right} was ${result}")
        assertEquals(+1, result)
    }

    @Test
    void testsingle() {
        log.debug("testing single cv numbers")
        CvNameComparator comp = new StringCvComparator()
        String left = "3, 4, 5"
        String right = "2"
        int result = comp.compare(left, right)
        log.debug("result of comparing ${left} with ${right} was ${result}")
        assertEquals(-1, result)
    }

    @Test
    void testBoth() {
        log.debug("testing single cv numbers")
        CvNameComparator comp = new StringCvComparator()
        String left = "3, 4, 5"
        String right = "3, 4, 6"
        int result = comp.compare(left, right)
        log.debug("result of comparing ${left} with ${right} was ${result}")
        assertEquals(-1, result)
    }

    @Test
    void testRight() {
        log.debug("testing single cv numbers")
        CvNameComparator comp = new StringCvComparator()
        String left = "1.5.11"
        String right = "3, 4, 5, 6"
        int result = comp.compare(left, right)
        log.debug("result of comparing ${left} with ${right} was ${result}")
        assertEquals(+1, result)
    }

}
