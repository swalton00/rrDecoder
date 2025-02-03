package com.spw.rr.database

import com.spw.rr.controllers.DataController.ViewType
import groovy.transform.ToString

import java.sql.Timestamp

@ToString(includeNames = true, includePackage = false, includeFields = true)
class DecoderEntry {
    Integer id
    String decoderId
    String fileName
    String roadNumber
    String roadName
    String manufacturer
    String owner
    String model
    String dccAddress
    String manufacturerId
    String productId
    Timestamp dateUpdated
    Timestamp detailTime
    Integer decoderTypeId
    Integer rosterId
    String hasDetail
    String hasSpeedProfile

    List<DecoderDef> defValues
    List<FunctionLabel> labelValues
    List<SpeedProfile> speedValues
    List<KeyValuePairs> keyPairs
    List<CvValues> cvValues

    Hashtable<String, AbstractItem> keyHash

    List<AbstractItem> getList(ViewType viewType) {
        switch (viewType) {
            case ViewType.ALL_CVS:
            case ViewType.STANDARD_CVS:
            case ViewType.SELECTED_CVS:
                return cvValues
                break
            case ViewType.DECODER_DETAIL:
                return defValues
                break
            case ViewType.FUNCTION_LABELS:
                return labelValues
                break
            case ViewType.SPEED_PROFILE:
                return speedValues
                break
            case ViewType.KEY_PAIRS:
                return keyPairs
                break
            default:
                throw new RuntimeException("unrecognized view type passed to getList - ${viewType}")
        }
    }
}

