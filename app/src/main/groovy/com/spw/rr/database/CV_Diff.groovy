package com.spw.rr.database

class CV_Diff extends AbstractDiff {
    String cvNumber


    @Override
    String getKey() {
        return cvNumber
    }

    @Override
    boolean wasChanged() {
        return false
    }
}
