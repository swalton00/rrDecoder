package com.spw.rr.viewdb

import com.spw.rr.utilities.DatabaseServices
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class ViewDbService {

    private static final Logger log = LoggerFactory.getLogger(ViewDbService.class)

    DatabaseServices baseDb = DatabaseServices.getInstance()


}
