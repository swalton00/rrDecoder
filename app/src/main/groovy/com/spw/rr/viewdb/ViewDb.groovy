package com.spw.rr.viewdb


import com.spw.rr.database.DecoderEntry

interface ViewDb {
    List<DecoderEntry> listDecodersByRosterID(List<Integer> array)
    List<DecoderEntry> ListWithCvs(Vector<Integer> decoderID, List<String> cvs, Boolean listAll)
}