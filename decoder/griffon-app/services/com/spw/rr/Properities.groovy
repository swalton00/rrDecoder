package com.spw.rr

import griffon.core.artifact.GriffonService
import griffon.metadata.ArtifactProviderFor
import org.slf4j.LoggerFactory
import org.slf4j.Logger

@javax.inject.Singleton
@ArtifactProviderFor(GriffonService.class)
class Properities {

    private static final Logger log = LoggerFactory.getLogger(Properties.class)

    public void savePropertiers() {

    }

    public void loadProperties() {

    }

    public String getProperty(String name) {

    }

    public void saveProperty(String name, String, value) {

    }
}
