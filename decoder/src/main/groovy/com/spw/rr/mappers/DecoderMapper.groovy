package com.spw.rr.mappers


import griffon.metadata.TypeProviderFor
import griffon.plugins.mybatis.MybatisMapper

@TypeProviderFor(MybatisMapper)
interface DecoderMapper extends MybatisMapper {
    int insertRosterEntry(RosterEntry entry)

    void updateRosterEntry(RosterEntry entry)

    List<RosterEntry> listRosters()

    List<RosterEntry> listSystemRoster(String systemName)

    RosterEntry getRosterEntry(int id)

    RosterEntry findRosterEntry(String systemName, String fullPath)

    int insertDecoderEntry(DecoderEntry entry)

    void updateDecoderEntry(DecoderEntry entry)

    int deleteDecoderEntry(DecoderEntry entry)

    List<DecoderEntry> listDecoders()

    List<DecoderEntry> listDecodersByRosterID(int[] rosterID)

    DecoderEntry getDecoderEntry(int id)

    int insertDecoderTypeEntry(DecoderType entry)

    void updateDecoderTypeEntry(DecoderType entry)

    List<DecoderType> listDecoderTypes()

    DecoderType getDecoderType(int id)

    DecoderType findDecoderType(String family, String model)

    int insertFunctionLabels(FunctionLabel newValue)

    List<FunctionLabel> listFunctionLabelsFor(List<Integer> parents)

    int insertSpeedSteps(SpeedProfile sp)

    List<SpeedProfile> listSpeedStepsFor(List<Integer> parents)

    int insertKeyValuePairs(KeyValuePairs kvp)

    List<KeyValuePairs> listKeyValuePairsFor(List<Integer> parents)

    void insertDecoderDef(DecoderDef decoderDef)

    List<DecoderDef> listDecoderDefFor(Integer parent)

    void insertCVs(CVvalues cVvalues)

    List<CVvalues> listCVsFor(Integer parent)
}