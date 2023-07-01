package com.spw.rr.mappers

import griffon.metadata.TypeProviderFor
import griffon.plugins.mybatis.MybatisMapper
import com.spw.rr.RosterEntry
import com.spw.rr.LocomotiveEntry
import com.spw.rr.DecoderEntry


@TypeProviderFor(MybatisMapper)
interface DecoderMapper extends MybatisMapper {
    int insertRosterEntry(RosterEntry entry)

    void updateRosterEntry(RosterEntry entry)

    List<RosterEntry> listSystemRoster(Integer systemId)

    RosterEntry getRosterEntry(int id)

    RosterEntry findRosterEntry(String systemName, String fullPath)

    int insertLocomotiveEntry(LocomotiveEntry entry)

    void updateLocomotiveEntry(LocomotiveEntry entry)

    int insertDecoderEntry(DecoderEntry entry)

    void updateDecoderEntry(DecoderEntry entry)

    List<RosterEntry> listRoster()

    List<LocomotiveEntry> listLocomotives(int rosterId)

    List<DecoderEntry> listDecoders()


    LocomotiveEntry getLocomotive(int id)

    DecoderEntry getDecoder(int id)

}