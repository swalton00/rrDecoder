package com.spw.rr.viewdb


import com.spw.rr.database.DecoderEntry
import com.spw.rr.utilities.DatabaseServices
import groovy.util.logging.Slf4j
import org.apache.ibatis.session.SqlSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Slf4j
@Singleton
class ViewDbService {

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
        KEY_VAL_LIST,   // key value pairs// decoder definitions
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

    /**
     * Retrieve a list of decoders with an array (lazy) of the specific Diff type
     * @param selectType    CV, SpecificCv, FunctionLabel, or KeyValue
     * @param howMany       either All, or only the changed keys
     * @param decoderIds    only for these decodors
     * @param cvList        optionally for only specific CVs
     * @return
     */
    ArrayList<DecoderEntry> getDecoderDiffs(ViewDb.SelectType selectType,
                                            ViewDb.DiffType howMany,
                                            ArrayList<Integer> decoderIds,
                                            ArrayList<String> cvList = null) {
        log.debug("getting the decoders with their diffs for ${selectType} with ${howMany} and decoderids ${decoderIds}")
        SqlSession session = null
        ArrayList<DecoderEntry> retVal
        try {
            session = baseDb.sqlSessionFactory.openSession(true)
            ViewDb mapper = session.getMapper(ViewDb.class)
            retVal = mapper.listDiffs(selectType, howMany, decoderIds, null)
            log.debug("result set is ${retVal}")
        } catch (Exception e) {
            log.error("Caught an exception attempting to retrieve the data", e)
        } finally {
            if (session) {
                session.close()
                return retVal
            }
        }
        return retVal
    }
}
