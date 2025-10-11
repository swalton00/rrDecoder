package com.spw.rr.database

import java.sql.Timestamp

interface ImportMapper {
    Timestamp getDBtime();
    void insertRosterEntry(RosterEntry entry)
    void updateRosterEntry(RosterEntry entry)
    int deleteDecoderEntry(DecoderEntry)
    ArrayList<FunctionLabel> getFunctionLabels(int decoderId)
    LabelVersion getLabelVersionFor(Integer decoderId)
}