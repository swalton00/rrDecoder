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
        log.debug("getting the list of rosters")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            List<RosterEntry> result = mapper.listRoster()
            log.debug("result found was ${result}")
            return result
        }
    }

    RosterEntry addRoster(RosterEntry entry) {
        log.debug("adding a new RosterEntry: ${entry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            int result = mapper.insertRosterEntry(entry)
            log.debug("result found was ${entry}")
            return entry
        }
    }

    void updateRosterEntry(RosterEntry entry) {

    }

    List<RosterEntry> listSystemRosters(String systemName) {

    }

    RosterEntry getRosterEntry(String systemName, String fullPath) {
        log.debug("Retrieving roster for ${systemName} with path of ${fullPath}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            log.trace("Parameter is ${systemName} and ${fullPath}")
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            RosterEntry result = mapper.findRosterEntry(systemName, fullPath)
            log.debug("result found was ${result}")
            return result
        }
    }

    LocomotiveEntry addLocomotiveEntry(LocomotiveEntry locomotiveEntry) {
        log.debug("adding a new Locomotive ${locomotiveEntry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            int result = mapper.insertLocomotiveEntry(locomotiveEntry)
            log.debug("result was ${locomotiveEntry}")
            return locomotiveEntry
        }
    }

    void updateLocomotiveEntry(LocomotiveEntry locomotiveEntry) {
        log.debug("updateing locomotive entry ${locomotiveEntry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            mapper.updateLocomotiveEntry(locomotiveEntry)
        }
    }

    int deleteLocomotiveEntry(LocomotiveEntry locomotiveEntry) {
        log.debug("deleting locomotive entry ${locomotiveEntry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            int result = mapper.deleteLocomotiveEntry(locomotiveEntry)
            log.debug("result was ${result}")
            return result
        }
    }

    List<LocomotiveEntry> loocomotivesForRoster(int rosterID) {
        log.debug("listing locomotives for rosterID ${rosterID}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            List<LocomotiveEntry> result = mapper.listLocomotivesByRosterID(rosterID)
            log.debug("result was ${result}")
            return result
        }
    }

    List<DecoderEntry> listDecoders() {
        log.debug("Retrieving list of decoders")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            return mapper.listDecoders()
        }
    }

    DecoderEntry insertDecoderEntry(DecoderEntry entry) {
        log.debug("adding new decoder: ${entry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            int result = mapper.insertDecoderEntry(entry)
            log.debug("result was ${entry}")
            return entry
        }
    }

    DecoderEntry getDecoder(int id) {
        log.debug("Retrieving decoder with id of ${fid}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            DecoderEntry result = mapper.getDecoder(id)
            log.debug("result found was ${result}")
            return result
        }
    }

    DecoderEntry findDecoder(String family, String model) {
        log.debug("Retrieving decoder entry with family: ${family} and model: ${model}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            DecoderEntry result = mapper.findDecoder(family, model)
            log.debug("result found was ${result}")
            return result
        }
    }

    void updateDecoderEntry(DecoderEntry entry) {
        log.debug("Updated decoder entry: ${entry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            mapper.updateDecoderEntry(entry)
            return
        }
    }

}
