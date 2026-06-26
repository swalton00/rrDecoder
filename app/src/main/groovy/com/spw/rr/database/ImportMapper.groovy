package com.spw.rr.database

import org.apache.ibatis.annotations.Param

import java.sql.Timestamp

interface ImportMapper {
    Timestamp getDBtime();
    void insertRosterEntry(RosterEntry entry)
    void updateRosterEntry(RosterEntry entry)
    int deleteDecoderEntry(DecoderEntry)
    int deleteOldLabels(DecoderEntry)
    int deleteOldKeys(DecoderEntry)
    ArrayList<FunctionLabel> getFunctionLabels(int decoderId)
    VersionBase getLastVersion(@Param("decoderId")int decoderId, @Param("tableName")String tableName)
}