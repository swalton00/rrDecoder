package com.spw.rr.utilities

import com.spw.rr.database.AbstractItem
import com.spw.rr.database.CvValues
import com.spw.rr.database.DecoderEntry
import com.spw.rr.database.FunctionLabel
import com.spw.rr.database.KeyValuePairs
import com.spw.rr.database.VersionBase
import groovy.util.logging.Slf4j

/**
 * Create a unique list of keys of some type from a list of lists, optionally sorted
 */
@Slf4j
class BuildKeyList {

    /**
     * Entry for no sublist, but sorting the data, (with no prior list)
     * @param mainList
     * @param retrieveList
     * @param retrieveKey
     * @param sortComparator
     * @return
     */
    static List<Object> buildList(List<Object> mainList,
                    Closure<List<Object>> retrieveList,
                    Closure<Object> retrieveKey,
                    Comparator sortComparator) {
        return buildList(mainList,
                null,
                retrieveList,
                null,
                retrieveKey,
                sortComparator,
                true,
                null,
                null)
    }

/**
 * Entry for returning a main list with no prior list
 * @param mainList
 * @param retrieveList
 * @param retrieveKey
 * @return
 */
    static List<Object> buildList(List<Object> mainList,
                    Closure<List<Object>> retrieveList,
                    Closure<Object> retrieveKey) {
        return buildList(mainList,
                null,
                retrieveList,
                null,
                retrieveKey,
                null,
                false,
                null,
                null)
    }

    static Closure retrieveValues = { DecoderEntry entry ->
        return entry.values
    }

    static Closure retrieveVersions = { DecoderEntry entry ->
        return entry.versions
    }

    static Closure retrieveDiffList = { VersionBase version ->
        return version.diffList
    }

    static Closure getKey = { Object item ->
        return item.key
    }

    static Closure getCVHash = { ->
        return new Hashtable<String, CvValues>()
    }

    static Closure putCVHash = { Object cv, Hashtable<String, CvValues> theHash ->
        theHash.put(cv.key, cv)
    }

    static Closure putLabelHash = { Object label, Hashtable<String, FunctionLabel> theHash ->
        theHash.put(label.getKey(), label)
    }

    static Closure getLabelHash = { ->
        return new Hashtable<String, FunctionLabel>()
    }

    static Closure getKeyHash =  {->
        return new Hashtable<String, KeyValuePairs>()
    }

    static Closure putKeyHash = { Object keyItem, Hashtable<String, KeyValuePairs> theHash ->
        theHash.put(keyItem.key, keyItem)
    }

    /**
     * buildList takes a list of lists, builds a list of unique values, optionally sorted
     * @param mainList        the list of lists to extract the keys
     * @param existingList    the keyList to be added to or null
     * @param retrieveList  a Closure that, given an entry in the main list, returns the list in question
     * @Param retrieveSubList A Closure that retrieves the subList (which itself is a list)
     * @param retrieveKey   a Closure that, given an entry in the sublist, returns the key value
     * @param sortComparator    a comparator for the sort
     * @param sorted        should it be sorted
     * @param getHashtable  returns a null table of the correct type (null if not hashtable needed)
     * @param putHashtable  required if using a hashtable - closure to insert into the hashtable
     * @return  an ArrayList of the unique values extracted from all the sublists
     *
     *      If retrieveList is null, the main list is used directly
     *      if retrieveSubList is null, the retrieveList has gotten the List<Object> desired
     *      If the retrieveKey is null, the complete Object entry
     *          extracted from the sublist is used as is
     *      The sortComparator should be left null to use the natural comparator from the object
     *      If the "sorted" is false, no sort is applied to the final list
     *
     *      process 1) build the list of lists
     *              2) build a HashSet of values extracted from the list
     *              3) optionally define a hashtable as a field in the parent object
     *              4) turn the HashSet into an ArrayList
     *              5) (optionally) sort the result
     *       Example:
     *              pass a list of DecoderEntry's
     *              pass in a Closure to retrieve the "values" field
     *                  that could be a list of CvValues, FunctionLabels, or KeyValuePairs
     *                  in that case, pass the sublist Closure as null
     *              the existingList field is null for a new list, or is an ArrayList to be extended
     *
     *          If a Hashtable is used (defined by the getHashtable Closure) it will be
     *              set as a field in the lowest parent. As keys are extracted, they will
     *              be placed in the Hashtable (using the putHashtable closure)
     */
    static List<Object> buildList(List<Object> mainList,
                    List<Object> existingList,
                    Closure<List<List<Object>>> retrieveList,
                    Closure<List<Object>> retrieveSubList,
                    Closure<Object> retrieveKey,
                    Comparator sortComparator,
                    Boolean sorted,
                    Closure<Hashtable<Object, Object>> getHashtable,
                    Closure putHashtable) {
        List<List<Object>> processList = createBasicList(existingList)
        if (retrieveSubList) {
            handleSubList(mainList,
                    processList,
                    retrieveList,
                    retrieveSubList,
                    retrieveKey,
                    getHashtable,
                    putHashtable )
        } else {
            processMain(mainList,
                    processList,
                    retrieveList,
                    retrieveKey,
                    getHashtable,
                    putHashtable)
        }
        HashSet<Object> listStart = new HashSet<Object>(processList)
        ArrayList<Object> finalList = new ArrayList<>(listStart)
        if (!sorted) {
            return finalList
        }
        finalList.sort(sortComparator)
        return finalList
    }
    /**
     * Create a new list if existing list is null
     * @param existingList a list of keys (may be null) usually Strings
     * @return
     */
    static private List<Object> createBasicList(List<Object> existingList) {
        if (!existingList) {
            return new ArrayList<Object>()
        } else {
            return existingList
        }
    }

    /**
     * process the list if the there is no sublist
     * @param theList       the entries to be process
     * @param processList   the list of extracted keys
     * @param retrieveList  a closure to retrieve the list to be processed
     * @param retrieveKey   a Closure to retrieve the key from the Object of the list
     * @param getHashtable  Closure taking no parameters, returning a Hashtable of the correct type
     * @return putHashtable Closure taking 2 parameters, theHash and the Item
     *
     */
    static private List<Object> processMain(List<Object> theList,
                                       List<Object> processList,
                                        Closure<List<Object>> retrieveList,
                                        Closure<Object> retrieveKey,
                                        Closure<Hashtable<Object, Object>> getHashtable,
                                        Closure putHashtable) {

        theList.each {Object theItem ->
            List<Object> toDoList = retrieveList(theItem)
            Hashtable<Object, Object> theHash
            if (getHashtable) {
                theHash = getHashtable()
                theItem.keyValues = theHash
            }
            toDoList.each { Object keyedItem ->
                processList.add(retrieveKey(keyedItem))
                if (putHashtable) {
                    putHashtable(keyedItem, theHash)
                }
            }
        }
        return processList
    }

    static private List<Object> handleSubList(List<Object> mainList,
                                              List<Object> existingList,
                                              Closure<List<Object>> retrieveList,
                                              Closure<List<Object>> retrieveSubList,
                                              Closure<Object> getKey,
                                              Closure<Hashtable<Object, Object>> getHashtable,
                                               Closure putHashtable) {
        mainList.each { Object thisOne ->
            (retrieveList(thisOne)).each { Object toDoList ->
                  processMain(toDoList as List<Object>,
                  existingList,
                  retrieveSubList,
                  getKey,
                  getHashtable,
                  putHashtable)
            }
        }
    }
}
