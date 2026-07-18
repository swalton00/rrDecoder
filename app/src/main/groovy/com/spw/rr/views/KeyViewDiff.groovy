package com.spw.rr.views

class KeyViewDiff extends AbstractViewDiff{

    String pair_key

    @Override
    String getKey() {
        return pair_key
    }

    @Override
    void setKey(String newKey) {
        pair_key = newKey
    }
}
