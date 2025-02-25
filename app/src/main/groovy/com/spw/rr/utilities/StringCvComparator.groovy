package com.spw.rr.utilities

import java.util.regex.Matcher
import java.util.regex.Pattern

class StringCvComparator extends CvNameComparator {
    @Override
    int compare(Object left, Object right) {
        Pattern regExPatt = Pattern.compile(match_pattern)
        Matcher leftMatch = regExPatt.matcher((String)left)
        Matcher rightMatch = regExPatt.matcher((String)right)
        if (leftMatch.matches() & rightMatch.matches()) {
            return super.compare(left, right)
        }
        if (leftMatch.matches()) {
            // right not not a cv number - so higher
            return +1
        }
        if (rightMatch.matches()) {
            // left doesn't match so lower
            return -1
        }
        return left.compareTo(right)
    }
}
