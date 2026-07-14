package com.spw.rr.database

class CV_Diff extends AbstractDiff {
    String cvNumber


    @Override
    String getKey() {
        return cvNumber
    }

    void setKey(String key) {
        cvNumber = key
    }

}
