package com.spw.rr

import com.spw.rr.mappers.DecoderMapper
import griffon.core.artifact.GriffonService
import griffon.plugins.mybatis.MybatisHandler
import org.apache.ibatis.session.SqlSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@javax.inject.Singleton
@griffon.metadata.ArtifactProviderFor(GriffonService)
class DecoderDBService {

    private static final Logger log = LoggerFactory.getLogger(DecoderDBService.class)

    @javax.inject.Inject
    private MybatisHandler handler

    List<RosterEntry> listRosters(){

    }

    void addRoster(RosterEntry entry) {

    }

    void updateRosterEntry(RosterEntry entry) {

    }

    List<RosterEntry> listSystemRosters(String systemName) {

    }

    RosterEntry getRosterEntry(String systemName, String fullPath) {
        log.debug("Retrieving roster for ${systemName} with path of ${fullPath}")
        RosterEntry entry = new RosterEntry()
        entry.fullPath = fullPath
        entry.systemName = systemName
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            RosterEntry result = session.getMapper(DecoderMapper).findRosterEntry(entry)
            log.debug("result found was ${result}")
            return result
        }
    }

}
