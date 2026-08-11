package com.spw.rr.viewdb


import com.spw.rr.database.DecoderEntry
import org.apache.ibatis.annotations.Param

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

    List<DecoderEntry> listDecodersByRosterID(@Param("array")List<Integer> array)
    List<DecoderEntry> listValues(@Param("selectType")SelectType selectType,
                                  @Param("ids")Vector<Integer> ids,
                                  @Param("cvs")List<String> cvs)
    List<DecoderEntry> listDecDiffs(@Param("selectType")SelectType selectType,
                                @Param("idList")List<Integer> idList,
                                @Param("doAll")Boolean doAll)
}