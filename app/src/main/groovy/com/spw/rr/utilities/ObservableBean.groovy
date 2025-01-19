package com.spw.rr.utilities

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class ObservableBean {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this)

    Boolean value = false
    String propertyName = "value"

    void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener)
    }

    void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener)
    }

    boolean getValue() {
        return metaPropertyValues
    }

    void setValue(boolean newValue) {
        Boolean oldValue = value
        value = newValue
        pcs.firePropertyChange(propertyName, oldValue, newValue)
    }
}
