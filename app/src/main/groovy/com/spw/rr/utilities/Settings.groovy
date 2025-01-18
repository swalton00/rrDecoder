package com.spw.rr.utilities

import groovy.transform.ToString
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ToString(includeFields = true, includeNames = true, includePackage = false, excludes = ["password"])
class Settings {
    private static final Logger log = LoggerFactory.getLogger(Settings.class)
    private static final PropertySaver saver = PropertySaver.getInstance()
    boolean settingsComplete = false
    boolean settingsValid = false
    boolean databaseOpen = false

    private static final String NAME_URL = "URL"
    private static final String NAME_USERID = "Userid"
    private static final String NAME_PASSWORD = "pw"
    private static final String NAME_SCHEMA = "Schema"

    String url
    String userid
    String password
    String schema

    public void loadSettings() {
        url = saver.getBaseString(NAME_URL)
        userid = saver.getBaseString(NAME_USERID)
        password = saver.getBaseString(NAME_PASSWORD)
        schema = saver.getBaseString(NAME_SCHEMA)
        if (url == null | userid == null | password == null | schema == null) {
            log.debug("settings not present on initial load")
            settingsComplete = null
        } else {
            log.debug("settings found complete on inital load")
            settingsComplete = true
        }
    }

    public void saveSettings() {
        log.debug("saving the settings values")
        saver.putBaseString(NAME_URL, url)
        saver.putBaseString(NAME_USERID, userid)
        saver.putBaseString(NAME_PASSWORD, password)
        saver.putBaseString(NAME_SCHEMA, schema)
    }
}
