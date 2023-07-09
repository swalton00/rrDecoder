package com.spw.rr

import com.spw.rr.mappers.DecoderEntry
import com.spw.rr.mappers.DecoderMapper
import com.spw.rr.mappers.DecoderType
import com.spw.rr.mappers.RosterEntry
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
        log.debug("updating the roster ${entry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            mapper.updateRosterEntry(entry)
        }
    }

    List<RosterEntry> listRosters(){
        log.debug("getting the list of rosters")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            List<RosterEntry> result = mapper.listRosters()
            log.debug("result found was ${result}")
            return result
        }
    }

    List<RosterEntry> listSystemRosters(String systemName) {
        log.debug("getting the list of rosters for system ${systemName}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            List<RosterEntry> result = mapper.listSystemRoster(systemName)
            log.debug("result found was ${result}")
            return result
        }
    }

    RosterEntry getRosterEntry(int rosterId) {
        log.debug("Retrieving roster with id of ${rosterId}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            RosterEntry result = mapper.getRosterEntry(rosterId)
            log.debug("result found was ${result}")
            return result
        }
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

    DecoderEntry addDecoderEntry(DecoderEntry decoderEntry) {
        log.debug("adding a new decoder ${decoderEntry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            int result = mapper.insertDecoderEntry(decoderEntry)
            log.debug("result was ${decoderEntry}")
            return decoderEntry
        }
    }

    void updateDecoderEntry(DecoderEntry decoderEntry) {
        log.debug("updating decoder entry ${decoderEntry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            mapper.updateDecoderEntry(decoderEntry)
        }
    }

    int deleteDecoderEntry(DecoderEntry decoderEntry) {
        log.debug("deleting decoder entry ${decoderEntry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            int result = mapper.deleteDecoderEntry(decoderEntry)
            log.debug("result was ${result}")
            return result
        }
    }

    List<DecoderEntry> decodersForRoster(int rosterID) {
        log.debug("listing decoders for rosterID ${rosterID}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            List<DecoderEntry> result = mapper.listDecodersByRosterID(rosterID)
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

    List<DecoderEntry> listDecodersByRosterID(int[] rosters) {
        log.debug("Retrieving list of decoders for ${rosters}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            return mapper.listDecodersByRosterID(rosters)
        }
    }

    DecoderEntry getDecoderEntry(int id) {
        log.debug("Finding a decoder with id of ${id}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            return mapper.getDecoderEntry(id)
        }
    }

    DecoderType insertDecoderTypeEntry(DecoderType entry) {
        log.debug("adding new decoder type: ${entry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            int result = mapper.insertDecoderTypeEntry(entry)
            log.debug("result was ${entry}")
            return entry
        }
    }

    void updateDecoderType(DecoderType entry) {
        log.debug("Updated decoder entry: ${entry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            mapper.updateDecoderTypeEntry(entry)
            return
        }
    }

    List<DecoderType> listDecoderTypes() {
        log.debug("returning a list of all decoder types")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            return mapper.listDecoderTypes()
        }
    }

    DecoderType getDecoderType(int id) {
        log.debug("Retrieving decoder with id of ${fid}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            DecoderType result = mapper.getDecoderType(id)
            log.debug("result found was ${result}")
            return result
        }
    }

    DecoderType findDecoderType(String family, String model) {
        log.debug("Retrieving decoder type entry with family: ${family} and model: ${model}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper =session.getMapper(DecoderMapper.class)
            DecoderType result = mapper.findDecoderType(family, model)
            log.debug("result found was ${result}")
            return result
        }
    }


}
