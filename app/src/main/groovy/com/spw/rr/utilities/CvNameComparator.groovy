package com.spw.rr.utilities

import java.util.regex.Matcher
import java.util.regex.Pattern

class CvNameComparator implements Comparator {
    private static int getValidCount(Matcher matcher) {
        if (matcher.group(3) != null) {
            return 3
        }
        if (matcher.group(2) != null) {
            return 2
        }
        return 1
    }

    private static int checkGroup(Matcher left, Matcher right, int groupNumber) {
        if (left.group(groupNumber) != null & right.group(groupNumber) == null) {
            return -1
        }
        if (left.group(groupNumber) == null & right.group(groupNumber) != null) {
            return +1
        }
        int leftValue = Integer.valueOf( left.group(groupNumber))
        int rightValue = Integer.valueOf( right.group(groupNumber))
        if (leftValue < rightValue) {
            return -1
        } else if (leftValue > rightValue) {
            return 1
        }
        return 0
    }

    final String match_pattern = "(\\d{1,3})(?:\\.(\\d{1,3}))?(?:\\.(\\d{1,3}))?"
/**
 *
 * @param left The left String
 * @param right The right String
 * @return 0 = both are equal
 *      -1 = left is less than
 *      +1 = left is greater than
 *      Inputs are CV values as extracted from DecoderPro stored XML
 *           May be one number (e.g. 3, 12, of 233)
 *           Or two numbers seperated by a period (8.24, 17.4, 0.4)
 *           Or three numbers, each group seperated by a period (0.2.17, 8.4.244)
 *           Numbers are always one to three digits
 *      Logic:
 *         If the number of groups is greater for one side:
 *             Return value - the lower is the one with the lesser number of groups
 *         If the number of match groups is the same:
 *             Compare numerically, group by group
 *             The lessor is first with a lower value for the group
 */
    @Override
    int compare(Object left, Object right) {


        Pattern regExPatt = Pattern.compile(match_pattern)

        if (!(left instanceof String)) {
            throw new RuntimeException("Left parameter must be a String")
        }
        if (!right instanceof String) {
            throw new RuntimeException("Right parameter must be a String")
        }
        Matcher leftMatch = regExPatt.matcher(left)

        if (!leftMatch.matches()) {
            throw new NotACvNameException("Left parameter must match a the CV name pattern")
        }
        Matcher rightMatch = regExPatt.matcher((String)right)
        if (!rightMatch.matches()) {
            throw new NotACvNameException("Right parameter must match the CV name pattern")
        }

        int leftCount = getValidCount(leftMatch)
        int rightCount = getValidCount(rightMatch)
        if (leftCount < rightCount) {
            return -1
        }
        if (leftCount > rightCount) {
            return +1
        }
        int retVal = checkGroup(leftMatch, rightMatch,1)
        if (retVal != 0) {
            return retVal
        }
        if ( leftCount <2) {
            return 0
        }
        retVal = checkGroup(leftMatch, rightMatch, 2)
        if (retVal != 0) {
            return retVal
        }
        if (leftCount < 3) {
            return 0
        }
        retVal = checkGroup(leftMatch, rightMatch, 3)
        return retVal
    }

}