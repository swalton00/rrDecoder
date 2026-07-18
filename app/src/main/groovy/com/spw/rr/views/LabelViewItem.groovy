package com.spw.rr.views

import com.spw.rr.database.AbstractDiff
import com.spw.rr.database.LabelDiff

class LabelViewItem extends AbstractViewItem{
   String functionNum
    String functionLabel
    Boolean locked

    @Override
    String getKey() {
        return functionNum
    }

    @Override
    void setKey(String key) {
        functionNum = key
    }

    @Override
    void addRows(List<String> thisLine) {
        log.info("LabelViewItem requested addRows - shouldn't happen")
    }

    @Override
    String getValue() {
        return functionLabel
    }

    @Override
    void setValue(String newValue) {
        functionLabel = newValue
    }

    @Override
    void setOldValue(AbstractDiff diff) {
        diff.oldValue = this.functionLabel
        ((LabelDiff)diff).oldLocked = this.locked
    }

    @Override
    void setNewValue(AbstractDiff diff) {
        diff.newValue = this.functionLabel
        ((LabelDiff)diff).newLocked = this.locked

    }
}
