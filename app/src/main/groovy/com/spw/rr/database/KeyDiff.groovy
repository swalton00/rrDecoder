package com.spw.rr.database

class KeyDiff extends AbstractDiff {

    String pairKey

    @Override
    String getKey() {
        return pairKey
    }

    void setKey(String key) {
        pairKey = key
    }
}
