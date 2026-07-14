package com.spw.rr.database

import com.spw.rr.database.VersionBase.WhichTable
import org.apache.ibatis.annotations.Param

import java.security.Key
import java.sql.Timestamp

interface Mapper {

    List<RosterEntry> listRosters()
    RosterEntry getRosterEntry(String systemName, String fullPath)
    RosterEntry getRosterEntryById(int rosterid)
    List<DecoderType> listDecoderTypes()
    List<DecoderEntry> listDecodersByRosterID(Integer id)
    List<DecoderEntry> listDecodersFor(List<Integer> rosterIds)
    DecoderEntry getDecoderEntry(int id)
    int insertDecoderEntry(DecoderEntry entry)
    int updateDecoderEntry(DecoderEntry entry)
    void insertFunctionLabel(FunctionLabel functionLabel)
    int updateFunctionLabel(FunctionLabel functionLabel)
    void insertFunctionVersion(VersionBase version)
    void insertLabelDiff(LabelDiff labelDiff)
    void insertKeyValuePairs(KeyValuePairs keyValuePairs)
    int updateKeyValuePairs(KeyValuePairs keyValuePairs)
    void insertKeyValueDiff(KeyDiff keyDiff)
    void insertCVDiff(CV_Diff cvDiff)
    void insertVersion(@Param("version")VersionBase, @Param("tableName")String tableName)
    int deleteObsoleteItems(@Param("table")String table,
                            @Param("columnName")String columnName,
                            @Param("decoderId")Integer decoderId,
                            @Param("obsoleteItems")ArrayList<String> obsoleteItems)
    RosterEntry findRosterEntry(String systemName, String fullPath)
    void insertDecoderTypeEntry(DecoderType)
    void insertSpeedProfile(SpeedProfile)
    int deleteCVs(Integer decoderId)
    void insertCVs(CvValues cVvalues)
    int updateCVs(CvValues cvValues)
    int updateDecoderDetailTime(Integer decoderId, Timestamp)
    VersionBase getLastVersion(@Param("decoderId")int decoderId,
                               @Param("tableName")String tableName)
    ArrayList<FunctionLabel> getFunctionLabels(int decoderId)
    ArrayList<KeyValuePairs> getKeyValuesFor(int decoderId)
    ArrayList<CvValues> getCvValuesFor(int decoderId)
}