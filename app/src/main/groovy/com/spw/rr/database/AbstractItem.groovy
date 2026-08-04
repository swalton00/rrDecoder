package com.spw.rr.database

import com.spw.rr.controllers.DataController.ViewType
import groovy.transform.ToString
import groovy.util.logging.Log4j
import org.slf4j.Logger

@ToString(includeNames = true, includePackage = false, includeFields = true)
@Log4j
abstract class AbstractItem  implements Comparable {

    Integer id
    Integer decoderId
    ArrayList<AbstractDiff> History

    abstract String  getKey();

    abstract void setKey(String key);

    abstract void addRows(List<String> thisLine);

    abstract String getValue();

    abstract void setValue(String newValue);

    abstract void setOldValue(AbstractDiff diff);

    abstract void setNewValue(AbstractDiff diff);

    int compareTo(Object other) {
        if (!(other instanceof AbstractItem)) {
            throw new RuntimeException("Attempt to compare an AbstractItem to something that is not an AbstractItem - ${other}")
        }
        return this.getKey().compareTo(other.getKey())
    }

}
