package com.spw.rr.views

class CvViewDiff extends AbstractViewDiff {
    String cvNumber
    String cvValue

    @Override
    String getKey() {
        return cvNumber
    }

    @Override
    void setKey(String newKey) {
        cvNumber = newKey
    }

}
