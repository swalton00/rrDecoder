package com.spw.rr.database

interface Mapper {

    List<RosterEntry> listRosters()
    RosterEntry getRosterEntry(String systemName, String fullPath)
    List<DecoderType> listDecoderTypes()
    void insertRosterEntry(RosterEntry entry)
    List<DecoderEntry> listDecodersByRosterID(Integer id)
    int insertDecoderEntry(DecoderEntry entry)
    int updateDecoderEntry(DecoderEntry)
    void insertFunctionLabel(FunctionLabel)
    void insertKeyValuePairs(KeyValuePairs)
    void insertSpeedSprofile(SpeedProfile)
    int deleteDecoderEntry(DecoderEntry)
    void updateDecoderEntry(DecoderEntry entry)
    void updateRosterEntry(RosterEntry entry)
    RosterEntry findRosterEntry(String systemName, String fullPath)
    void insertDecoderTypeEntry(DecoderType)
    void insertSpeedProfile(SpeedProfile)
}