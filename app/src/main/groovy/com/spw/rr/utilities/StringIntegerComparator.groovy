package com.spw.rr.utilities

import groovy.util.logging.Log4j

import java.util.regex.Matcher
import java.util.regex.Pattern

@Log4j
class StringIntegerComparator implements Comparator {

/**
 *
 * @param left The left String
 * @param right The right String
 * @return 0 = both are equal
 *      -1 = left is less than
 *      +1 = left is greater than
 *      Inputs are Strings but they need to be compared as Integers
 *
 *      Logic:
 *         if left and right both successfully convert to Integer:
 *             return Integer.compare(left, right)
 *         otherwise return String.compare(left, right)
 */
    @Override
    int compare(Object left, Object right) {
        try {
            Integer leftInt = Integer.valueOf(left)
            Integer rightInt = Integer.valueOf(right)
            return Integer.compare(leftInt, rightInt)
        }  catch (NumberFormatException ex) {
            log.info("Number Format Exception attempting to compare Strings as Integers - left ${left} right ${right}", ex)
            return left.compareTo(right)
        }


    }

}