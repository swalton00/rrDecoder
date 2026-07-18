package com.spw.rr.views

class LabelViewDiff extends AbstractViewDiff{

    String functionNum
    Boolean oldLocked
    Boolean newLocked

    @Override
    String getKey() {
        return functionNum
    }

    @Override
    void setKey(String newKey) {
        functionNum = newKey
    }
}
