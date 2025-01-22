package com.spw.rr.utilities

import com.sun.org.slf4j.internal.LoggerFactory


import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Singleton
class PropertySaver {

    private static final Logger log = LoggerFactory.getLogger(PropertySaver.class)

    static final String PROP_FILE_NAME = "rrDecoder.properties"
    static final String HOME_DIR = ".rrdecoder"

    Properties properties = new Properties()
    boolean dirty = false
    boolean inited = false

    private File getHomeLocation() {
        log.trace("getting user home directory")
        String homeLocation = System.getProperty("user.home")
        log.debug("found location is ${homeLocation}")
        File homeFile = new File(homeLocation)
        File rrHome = new File(homeFile, HOME_DIR)
        if (!rrHome.exists()) {
            log.debug("RR Home directory does not yet exist -- creating")
            boolean created = rrHome.mkdir()
            log.debug("Directory ${rrHome.toString()} was created - ${created}")
        }
        return rrHome
    }

    public void init() {
        log.debug("Initializing the PropertySaver")
        InputStream inputStream
        try {
            inputStream = new FileInputStream(new File(getHomeLocation(), PROP_FILE_NAME))
        } catch (FileNotFoundException exception) {
            log.debug("did not find an existing Properties file")
        }
        if (inputStream != null) {
            try {
                properties.load(inputStream)
                inputStream.close()
            } catch (IOException ex) {
                log.error("IO error loading properties", ex)
            }
            inputStream.close()
        }
        inited = true
        dirty = false
    }

    public void writeValues() {
        if (dirty) {
            log.debug("writing the properties file")
            FileOutputStream outputStream = new FileOutputStream(new File(getHomeLocation(), PROP_FILE_NAME))
            properties.store(outputStream, "OpsCar saved values")
            outputStream.close()
        } else {
            log.debug("skipped properties write as it was unchanged")
        }
    }


    private void checkLoaded() {
        if (inited) {
            return
        }
        log.error("call to property saver without an initialization")
        throw new RuntimeException("call to PropertySaver without an initial init call")
    }

    void putField(String prefix, String name, String value) {
        log.debug("saving a value for ${prefix} name of ${name} value ${value} ")
        checkLoaded()
        properties.put(prefix + "." + name, value)
        dirty = true
    }

    String getField(String prefix, String name) {
        log.trace("Retrieving a value for ${prefix} ${name}")
        checkLoaded()
        return properties.get(prefix + "." + name)
    }

    void putInt(String prefix, String name, int value) {
        //log.trace("saving an integer for${prefix} - ${name}")
        checkLoaded()
        properties.put(prefix + "." + name, Integer.toString(value))
        dirty = true
    }

    Integer getInt(String prefix, String name) {
        log.debug("getting an integer for ${prefix} - ${name}")
        checkLoaded()
        String retVal
        retVal = properties.get(prefix + "." + name)
        if (retVal != null) {
            return Integer.valueOf(retVal)
        } else return null
    }

    String getBaseString(String key) {
        log.debug("Getting a property value ${key}")
        checkLoaded()
        return properties.get(key)
    }

    void putBaseString(String key, String value) {
        log.debug("saving a base String - key = ${key}")
        checkLoaded()
        properties.put(key, value)
        dirty = true
    }



}
