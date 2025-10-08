package com.spw.rr.database

interface Mapper {

    List<RosterEntry> listRosters()
    RosterEntry getRosterEntry(String systemName, String fullPath)
    RosterEntry getRosterEntryById(int rosterid)
    List<DecoderType> listDecoderTypes()
    List<DecoderEntry> listDecodersByRosterID(Integer id)
    List<DecoderEntry> listDecodersFor(List<Integer> rosterIds)
    DecoderEntry getDecoderEntry(int id)
    int insertDecoderEntry(DecoderEntry entry)
    int updateDecoderEntry(DecoderEntry)
    void insertFunctionLabel(FunctionLabel)
    void insertKeyValuePairs(KeyValuePairs)
    void insertSpeedSprofile(SpeedProfile)

    void updateDecoderEntry(DecoderEntry entry)
    RosterEntry findRosterEntry(String systemName, String fullPath)
    void insertDecoderTypeEntry(DecoderType)
    void insertSpeedProfile(SpeedProfile)
    int deleteCVs(Integer decoderId)
    int deleteDecoderDef(Integer decoderId)
    void insertDecoderDef(DecoderDef decoderDef)
    void insertCVs(CvValues cVvalues)
    int updateDecoderDetailTime(Integer decoderId)
}