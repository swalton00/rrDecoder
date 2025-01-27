package com.spw.rr.viewdb


import com.spw.rr.database.DecoderEntry
import com.spw.rr.utilities.DatabaseServices
import org.apache.ibatis.session.SqlSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class ViewDbService {

    private static final Logger log = LoggerFactory.getLogger(ViewDbService.class)

    DatabaseServices baseDb = DatabaseServices.getInstance()

    List<DecoderEntry> listDecoderByRosterId(List<Integer> ids) {
        log.debug("getting a list of decoders for the following rosterIds: ${ids}")
        SqlSession session = null
        List<DecoderEntry> entries
        try {
            session = baseDb.sqlSessionFactory.openSession()
            ViewDb map = session.getMapper(ViewDb.class)
            entries = map.listDecodersByRosterID(ids)
            log.debug("got a list of size ${entries.size()}")
        } finally {
            if (session != null) {
                log.debug("closing the sesssion")
                session.close()
            }
        }
        return entries
    }

    List<DecoderEntry> listStandardCVs(Vector<Integer> decoderIds, List<String> cvList, boolean listall) {
        log.debug("retrieving CVs for ${decoderIds}")
        SqlSession session
        List<DecoderEntry> retvals
         try {
            session = baseDb.sqlSessionFactory.openSession(true)
             ViewDb map = session.getMapper(ViewDb.class)
             retvals = map.ListWithCvs(decoderIds, cvList, listall)
        } finally {
            session.close()
         }
        log.trace("returned list is ${retvals}")
        log.debug("returning a list of ${retvals.size()}")
        return retvals
    }
}
