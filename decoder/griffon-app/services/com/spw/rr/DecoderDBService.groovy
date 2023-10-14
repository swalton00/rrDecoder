package com.spw.rr

import com.spw.rr.mappers.CVvalues
import com.spw.rr.mappers.DecoderDef
import com.spw.rr.mappers.DecoderEntry
import com.spw.rr.mappers.DecoderMapper
import com.spw.rr.mappers.DecoderType
import com.spw.rr.mappers.FunctionLabel
import com.spw.rr.mappers.KeyValuePairs
import com.spw.rr.mappers.RosterEntry
import com.spw.rr.mappers.SpeedProfile
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

    private SqlSession currentSession = null

    RosterEntry transAddRoster(RosterEntry entry) {
        log.debug("adding a new roster entry as part of a transaction")
        if (currentSession == null) {
            throw new RuntimeException("transaction but current session is null")
        }
        DecoderMapper mapper = currentSession.getMapper(DecoderMapper.class)
        int result = mapper.insertRosterEntry(entry)
        log.debug("result was ${entry}")
        return entry
    }

    RosterEntry addRoster(RosterEntry entry) {
        log.debug("adding a new RosterEntry: ${entry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            int result = mapper.insertRosterEntry(entry)
            log.debug("result found was ${entry}")
            return entry
        }
    }

    void beginTransaction() {
        log.debug("beginning a transaction")
        if (currentSession == null) {
            currentSession = handler.getSqlSession("default", false)
        }
    }

    void commitWork() {
        log.debug("now committing current work")
        if (currentSession == null) {
            throw new RuntimeException("Attempting to commit work when not in a transaction")
        }
        currentSession.commit()
    }

    void closeSession() {
        log.debug("closing current session")
        if (currentSession == null) {
            throw new RuntimeException("attempting to close a not-open session")
        }
        currentSession.close()
    }

    void rollbackAll() {
        if (currentSession == null) {
            throw new RuntimeException("Attempting to rollback work when not in a transaction")
        }
        currentSession.rollback()
    }

    void transUpdateRosterEntry(RosterEntry rosterEntry) {
        log.debug("Updating roster entry with ID of ${rosterEntry.id} within a transaction")
        if (currentSession == null) {
            throw new RuntimeException("transaction but current session is null")
        }
        DecoderMapper mapper = currentSession.getMapper(DecoderMapper.class)
        mapper.updateRosterEntry(rosterEntry)
    }

    void updateRosterEntry(RosterEntry entry) {
        log.debug("updating the roster ${entry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            mapper.updateRosterEntry(entry)
        }
    }

    List<RosterEntry> listRosters() {
        log.debug("getting the list of rosters")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            List<RosterEntry> result = mapper.listRosters()
            log.debug("result found was ${result}")
            return result
        }
    }

    List<RosterEntry> listSystemRosters(String systemName) {
        log.debug("getting the list of rosters for system ${systemName}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            List<RosterEntry> result = mapper.listSystemRoster(systemName)
            log.debug("result found was ${result}")
            return result
        }
    }

    RosterEntry getRosterEntry(int rosterId) {
        log.debug("Retrieving roster with id of ${rosterId}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            RosterEntry result = mapper.getRosterEntry(rosterId)
            log.debug("result found was ${result}")
            return result
        }
    }

    RosterEntry getRosterEntry(String systemName, String fullPath) {
        log.debug("Retrieving roster for ${systemName} with path of ${fullPath}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            log.trace("Parameter is ${systemName} and ${fullPath}")
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            RosterEntry result = mapper.findRosterEntry(systemName, fullPath)
            log.debug("result found was ${result}")
            return result
        }
    }

    DecoderEntry addDecoderEntry(DecoderEntry decoderEntry) {
        log.debug("adding a new decoder ${decoderEntry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            int result = mapper.insertDecoderEntry(decoderEntry)
            log.debug("result was ${decoderEntry}")
            return decoderEntry
        }
    }

    DecoderEntry transAddDecoderEntry(DecoderEntry decoderEntry) {
        log.debug("adding decoder entry within a transaction data is ${decoderEntry}")
        if (currentSession == null) {
            throw new RuntimeException("attempting to run transInsertFunctionLabels outside of a transaction")
        }
        DecoderMapper mapper = currentSession.getMapper(DecoderMapper.class)
        int result = mapper.insertDecoderEntry(decoderEntry)
        log.debug("returning decoder with id of ${decoderEntry.id}")
        return decoderEntry
    }

    void transUpdateDecoderEntry(DecoderEntry decoderEntry) {
        log.debug("updating a decoder entry with id of ${decoderEntry.id} in a transaction")
        if (currentSession == null) {
            throw new RuntimeException("attempting to run transInsertFunctionLabels outside of a transaction")
        }
        DecoderMapper mapper = currentSession.getMapper(DecoderMapper.class)
        mapper.updateDecoderEntry(decoderEntry)
    }

    void updateDecoderEntry(DecoderEntry decoderEntry) {
        log.debug("updating decoder entry ${decoderEntry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            mapper.updateDecoderEntry(decoderEntry)
        }
    }

    int transDeleteDecoderEntry(DecoderEntry decoderEntry) {
        log.debug("Deleting decoder with id of ${decoderEntry.id} within a transaction")
        if (currentSession == null) {
            throw new RuntimeException("transaction but current session is null")
        }
        DecoderMapper mapper = currentSession.getMapper(DecoderMapper.class)
        int result = mapper.deleteDecoderEntry(decoderEntry)
        log.debug("return result of ${result}")
        return result
    }

    int deleteDecoderEntry(DecoderEntry decoderEntry) {
        log.debug("deleting decoder entry ${decoderEntry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            int result = mapper.deleteDecoderEntry(decoderEntry)
            log.debug("result was ${result}")
            return result
        }
    }

    List<DecoderEntry> decodersForRoster(int rosterID) {
        log.debug("listing decoders for rosterID ${rosterID}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
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
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            int result = mapper.insertDecoderTypeEntry(entry)
            log.debug("result was ${entry}")
            return entry
        }
    }

    void updateDecoderType(DecoderType entry) {
        log.debug("Updated decoder entry: ${entry}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            mapper.updateDecoderTypeEntry(entry)
            return
        }
    }

    List<DecoderType> listDecoderTypes() {
        log.debug("returning a list of all decoder types")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            return mapper.listDecoderTypes()
        }
    }

    DecoderType getDecoderType(int id) {
        log.debug("Retrieving decoder with id of ${fid}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            DecoderType result = mapper.getDecoderType(id)
            log.debug("result found was ${result}")
            return result
        }
    }

    DecoderType findDecoderType(String family, String model) {
        log.debug("Retrieving decoder type entry with family: ${family} and model: ${model}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            DecoderType result = mapper.findDecoderType(family, model)
            log.debug("result found was ${result}")
            return result
        }
    }

    FunctionLabel transInsertFunctionLabels(FunctionLabel newValue) {
        log.debug("inserting new FunctionLabels as part of a transaction - ${newValue}")
        if (currentSession == null) {
            throw new RuntimeException("attempting to run transInsertFunctionLabels outside of a transaction")
        }
        DecoderMapper mapper = currentSession.getMapper(DecoderMapper.class)
        int result = mapper.insertFunctionLabels(newValue)
        log.debug("returning result: ${newValue}")
        return newValue
    }

    FunctionLabel insertFunctionLabels(FunctionLabel newValue) {
        log.debug("adding new function label: ${newValue}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            int result = mapper.insertFunctionLabels(newValue)
            log.debug("result was ${newValue}")
            return newValue
        }
    }

    List<FunctionLabel> listFunctionLabelsFor(List<Integer> parents) {
        log.debug("returning a list of all function labels for the following parents: ${parents}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            return mapper.listFunctionLabelsFor(parents)
        }
    }

    SpeedProfile insertSpeedProfile(SpeedProfile sp) {
        log.debug("adding new speed profile: ${sp}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            mapper.insertSpeedSteps(sp)
            log.debug("result was ${sp}")
            return sp
        }
    }

    SpeedProfile transInsertSpeedProfile(SpeedProfile sp) {
        log.debug("adding a new SpeedProfile: ${sp}")
        if (currentSession == null) {
            throw new RuntimeException("attempting to add a speed profile outside a transaction")
        }
        DecoderMapper mapper = currentSession.getMapper(DecoderMapper.class)
        mapper.insertSpeedSteps(sp)
        log.debug("inserted value was ${sp}")
        return sp
    }

    List<SpeedProfile> listSpeedProfilesFor(List<Integer> parents) {
        log.debug("returning a list of speed profiles for the following parents: ${parents}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            return mapper.listSpeedStepsFor(parents)
        }
    }

    KeyValuePairs insertKeyValuePair(KeyValuePairs kvp) {
        log.debug("adding new key value pair: ${kvp}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            mapper.insertKeyValuePairs(kvp)
            log.debug("result was ${kvp}")
            return kvp
        }
    }

    KeyValuePairs transInsertKeyValuePair(KeyValuePairs kvp) {
        log.debug("adding a new KeyValuePair: ${kvp}")
        if (currentSession == null) {
            throw new RuntimeException("attempting to insert a new KeyValuePair outside a transaction")
        }
        DecoderMapper mapper = currentSession.getMapper(DecoderMapper.class)
        mapper.insertKeyValuePairs(kvp)
        log.debug("inserted value was ${kvp}")
        return kvp
    }


    List<KeyValuePairs> listKeyValuePairsFor(List<Integer> parents) {
        log.debug("returning a list of key value pairs for the following parents: ${parents}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            return mapper.listKeyValuePairsFor(parents)
        }
    }

    DecoderDef transInsertDecoderDef(DecoderDef decoderDef) {
        log.debug("adding new decoder defininiton: ${decoderDef} inside a transaction")
        if (currentSession == null) {
            throw new RuntimeException("attempting to insert a new KeyValuePair outside a transaction")
        }
        DecoderMapper mapper = currentSession.getMapper(DecoderMapper.class)
        mapper.insertDecoderDef(decoderDef)
        log.debug("result was ${decoderDef}")
        return decoderDef
    }


    DecoderDef insertDecoderDef(DecoderDef decoderDef) {
        log.debug("adding new decoder defininiton: ${decoderDef}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            mapper.insertDecoderDef(decoderDef)
            log.debug("result was ${decoderDef}")
            return decoderDef
        }
    }

    List<DecoderDef> listDecoderDefFor(Integer parent) {
        log.debug("returning a list of decoder definitions for parent: ${parent}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            return mapper.listDecoderDefFor(parent)
        }
    }


    CVvalues transInsertCVs(CVvalues cVvalues) {
        log.debug("adding new CV value: ${cVvalues} for a transaction")
        if (currentSession == null) {
            throw new RuntimeException("attempting to insert a new KeyValuePair outside a transaction")
        }
        DecoderMapper mapper = currentSession.getMapper(DecoderMapper.class)
        mapper.insertCVs(cVvalues)
        log.debug("result was ${cVvalues}")
        return cVvalues

    }

    CVvalues insertCVs(CVvalues cVvalues) {
        log.debug("adding new CV value: ${cVvalues}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            mapper.insertCVs(cVvalues)
            log.debug("result was ${cVvalues}")
            return cVvalues
        }
    }

    List<CVvalues> listCVsFor(Integer parent) {
        log.debug("returning a list of cvs for parent: ${parent}")
        handler.withSqlSession { String sessionFactoryName, SqlSession session ->
            DecoderMapper mapper = session.getMapper(DecoderMapper.class)
            return mapper.listCVsFor(parent)
        }
    }

    int transUpdateDetailTime(Integer decoderId) {
        log.debug("update detail timestamp for decoder id ${decoderId} in a transaction")
        if (currentSession == null) {
            throw new RuntimeException("attempting to insert a new KeyValuePair outside a transaction")
        }
        DecoderMapper mapper = currentSession.getMapper(DecoderMapper.class)
        return mapper.updateDecoderDetailTime(decoderId)
    }
}
