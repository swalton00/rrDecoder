package com.spw.rr.viewdb

import com.spw.rr.database.DecoderEntry

interface ViewDb {
    List<DecoderEntry> listDecodersByRosterID(List<Integer> array)

}