package com.spw.rr.views

import com.spw.rr.database.AbstractDiff

class KeyViewItem extends AbstractViewItem{
    String pair_key
    String pair_value

    @Override
    String getKey() {
        return pair_key
    }

    @Override
    void setKey(String key) {
        pair_key = key
    }

    @Override
    void addRows(List<String> thisLine) {
        log.info("called KeyViewItem for an addRows - shouldn't happen")
    }

    @Override
    String getValue() {
        return pair_value
    }

    @Override
    void setValue(String newValue) {
        pair_value = newValue
    }

    @Override
    void setOldValue(AbstractDiff diff) {
        diff.oldValue = this.pair_value
    }

    @Override
    void setNewValue(AbstractDiff diff) {
        diff.newValue = this.pair_value
    }
}
