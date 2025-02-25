package com.spw.rr.utilities

import static org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TestCvNameComparator {
    private static final Logger log = LoggerFactory.getLogger(TestCvNameComparator.class)

    @Test
    void testBasicComparator() {
        log.debug("testing single cv numbers")
        CvNameComparator comp = new CvNameComparator()
        String left = "3"
        String right = "2"
        int result  = comp.compare(left, right)
        log.debug("result of comparing ${left} with ${right} was ${result}")
        assertEquals(+1, result)
    }

    @Test
    void testOneComparator() {
        log.debug("testing single cv numbers")
        CvNameComparator comp = new CvNameComparator()
        String left = "5.11"
        String right = "5.16"
        int result = comp.compare(left, right)
        log.debug("result of comparing ${left} with ${right} was ${result}")
        assertEquals(-1, result)
    }

}
