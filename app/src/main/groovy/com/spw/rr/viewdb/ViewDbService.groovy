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
        } catch (Exception e) {
            log.error("Exception process the SQL", e)
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
        } catch (Exception e) {
             log.error("Exception process the SQL", e)
         } finally {
            session.close()
         }
        log.trace("returned list is ${retvals}")
        log.debug("returning a list of ${retvals.size()}")
        return retvals
    }

    enum ListType {
        LABEL_LIST,     // function labels
        SPEED_LIST,     // speed profiles
        KEY_VAL_LIST,   // key value pairs
        DEF_LIST,       // decoder definitions
        FIXED_CVS,      // standard cvs
        CV_LIST,        // list of cvs
        ALL_CV              // used by DataController to build that list
    }

    List<DecoderEntry> getList(ListType listType, Vector<Integer> decoderIds, ArrayList<String> cvList) {
        log.debug("getting a list of decoders  ${decoderIds} with type of ${listType}")
        SqlSession session
        List<DecoderEntry> retVal
        try {
            session = baseDb.sqlSessionFactory.openSession(true)
            ViewDb map = session.getMapper(ViewDb.class)
            switch (listType) {
                case ListType.LABEL_LIST :
                    retVal = map.listValues(ViewDb.SelectType.SELECT_FUNC, decoderIds, null)
                    break
                case ListType.SPEED_LIST :
                    retVal = map.listValues(ViewDb.SelectType.SELECT_SPD,decoderIds, null)
                    break
                case ListType.KEY_VAL_LIST :
                    retVal = map.listValues(ViewDb.SelectType.SELECT_KEY, decoderIds, null)
                    break
                case ListType.DEF_LIST :
                    retVal = map.listValues(ViewDb.SelectType.SELECT_DEF, decoderIds,null )
                    break
                case ListType.FIXED_CVS :
                    retVal = map.listValues(ViewDb.SelectType.SELECT_FXD_CVS, decoderIds, null)
                    break
                case ListType.CV_LIST :
                    retVal = map.listValues(ViewDb.SelectType.SELECT_SEL_CVS, decoderIds, cvList)
                    break
                case ListType.ALL_CV :
                    retVal = map.listValues(ViewDb.SelectType.SELECT_ALL_CVS, decoderIds, null)
                    break
                default:
                    throw new RuntimeException("unrecogized case in ListType")
            }
            log.debug("list size is ${retVal.size()} and session is ${session}")
        } catch (Exception e) {
            log.error("Exception process the SQL", e)
        } finally {
            session.close()
        }
        log.debug("returning a list with ${retVal.size()} entries")
        return retVal
    }
}
