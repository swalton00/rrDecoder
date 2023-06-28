package com.spw.rr

import griffon.core.GriffonApplication
import griffon.core.artifact.GriffonService
import griffon.metadata.ArtifactProviderFor
import org.slf4j.LoggerFactory
import org.slf4j.Logger

@javax.inject.Singleton
@ArtifactProviderFor(GriffonService.class)
class PropertiesService {

    static final String PROP_NAME = "RRdecoder.properties"
    static final String PROP_USER = "Userid"
    static final String PROP_PW = "Password"
    static final String PROP_URL = "URL"

    private static final Logger log = LoggerFactory.getLogger(PropertiesService.class)

    Properties properties = new Properties()
    boolean inited = false

    private void checkLoaded() {
        if (inited) {
            return
        }
        log.debug("loading the properties file")
        InputStream inputStream
        try {
            inputStream = new FileInputStream(PROP_NAME)
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
        } else {
            properties.setProperty(PROP_USER, "")
            properties.setProperty(PROP_PW, "")
            properties.setProperty(PROP_URL, "")
        }
        inited = true
    }

    private void saveAllValues() {
        log.debug("now storing the properties")
        if (!inited) {
            return
        }
        try {
            FileOutputStream propertyStream = new FileOutputStream(PROP_NAME)
            properties.store(propertyStream, "rrDecoderProperties")
        } catch (Exception e) {
            log.error("Error storing the Properties file", e)
        }
    }

    public void onShutdownStart(GriffonApplication application) {
        log.debug("shutting down the PropertiesService -- saving the properties file")
        saveAllValues()
    }

    public void saveProperties() {
        saveAllValues()
    }

    public String getPropertyUser() {
        checkLoaded()
        return properties.getProperty(PROP_USER)
    }

    public String getPropertyPassword() {
        checkLoaded()
        return properties.getProperty(PROP_PW)
    }

    public String getPropertyURL() {
        checkLoaded()
        return properties.getProperty(PROP_URL)
    }

    public void storeUser(String value) {
        checkLoaded()
        properties.setProperty(PROP_USER, value)
    }

    public void storePassword(String value) {
        checkLoaded()
        properties.setProperty(PROP_PW, value)
    }

    public void storeURL(String value) {
        checkLoaded()
        properties.setProperty(PROP_URL, value)
    }

}
