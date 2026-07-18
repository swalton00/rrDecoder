package com.spw.rr.viewdb


import com.spw.rr.database.DecoderEntry

interface ViewDb {
    enum  SelectType {
        SELECT_ALL_CVS,
        SELECT_FXD_CVS,
        SELECT_SEL_CVS,
        SELECT_FUNC,
        SELECT_SPD,
        SELECT_KEY
    }

    enum DiffType {
        ONLY_CHANGED,
        ALL_VALUES
    }

    List<DecoderEntry> listDecodersByRosterID(List<Integer> array)
    List<DecoderEntry> listWithCvs(Vector<Integer> decoderID, List<String> cvs, Boolean listAll)
    List<DecoderEntry> listValues(selectType, Vector<Integer> ids, List<String> cvs)
    List<DecoderEntry> listDiffs(SelectType selectType, DiffType diffType, List<Integer> ids, List<String> cvs)
}