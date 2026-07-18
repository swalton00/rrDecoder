package com.spw.rr

import com.spw.rr.database.DecoderEntry
import com.spw.rr.utilities.ApplyResources
import com.spw.rr.viewdb.ViewDb
import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.slf4j.LoggerFactory
import org.slf4j.Logger

import java.sql.Connection

class DbService {
    private static Logger log = LoggerFactory.getLogger(DbService.class)

    private static SqlSessionFactory sqlSessionFactory
    private static final String MYBATIS_RESOURCE = "mybatis.xml"
    private static final String DATA_RESOURCE = "insertData.sql"
    private static final String URL = "jdbc:h2:mem:testdb;INIT=create schema decoder\\;set schema decoder\\;runscript from 'src/main/resources/createTables.sql'"
    private static final String USERID = "test"
    private static final String PASSWORD = "testpw"

    void dbStart() {
        log.debug("setting up the database resources")
        InputStream inputStream = Resources.getResourceAsStream(MYBATIS_RESOURCE)
        Properties props = new Properties()
        props.setProperty("userid", USERID)
        props.setProperty("password", PASSWORD)
        props.setProperty("url", URL)

        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, "test", props)
        SqlSession session
        ApplyResources resources = new ApplyResources()
            try {
                session = sqlSessionFactory.openSession()
                Connection conn = session.getConnection()
                resources.apply(DATA_RESOURCE, conn)
            } finally {
                session.close()
            }
        }


    List<DecoderEntry> listWithFixed(List<Integer> decoderIds) {
        log.debug("listing with fixed values for ids ${decoderIds}")
        SqlSession session
        List<DecoderEntry> returnValues
        try {
            session = sqlSessionFactory.openSession()
            ViewDb mapper = session.getMapper(ViewDb.class)
            returnValues = mapper.listWithCvs(decoderIds, null, false)
        } finally {
            session.close()
        }
        log.debug("retuning a list of size ${returnValues.size()}")
        return returnValues
    }

    List<DecoderEntry> listWithParameters(Vector<Integer> decoderIds, List<String> parameters) {
        log.debug("listing with fixed values for ids ${decoderIds}")
        SqlSession session
        List<DecoderEntry> returnValues
        try {
            session = sqlSessionFactory.openSession()
            ViewDb mapper = session.getMapper(ViewDb.class)
            returnValues = mapper.listWithCvs(decoderIds, parameters, Boolean.valueOf(false))
        } finally {
            session.close()
        }
        log.debug("retuning a list of size ${returnValues.size()}")
        return returnValues
    }

    List<DecoderEntry> listValues(ViewDb.SelectType selectType, Vector<Integer> ids, List<String> cvs) {
        log.debug("select list of type ${selectType} for ids ${ids}")
        SqlSession session
        List<DecoderEntry> results
        try {
            session = sqlSessionFactory.openSession()
            ViewDb mapper = session.getMapper(ViewDb.class)
            results = mapper.listValues(selectType, ids, cvs)
        } finally {
            session.close()
        }
        log.debug("returning ${results.size()} rows")
        return  results
    }

}
