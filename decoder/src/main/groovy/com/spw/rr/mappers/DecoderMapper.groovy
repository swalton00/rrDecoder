package com.spw.rr.mappers


import griffon.metadata.TypeProviderFor
import griffon.plugins.mybatis.MybatisMapper
import com.spw.rr.RosterEntry
import com.spw.rr.DecoderEntry
import com.spw.rr.DecoderType


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

}