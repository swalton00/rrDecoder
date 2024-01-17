package com.spw.rr

import com.spw.rr.database.CreateTables
import griffon.plugins.mybatis.MybatisBootstrap
import org.apache.ibatis.session.SqlSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Nonnull
import javax.inject.Named
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

@Named("myBootstrap")
class RRMybatisBootstrap implements MybatisBootstrap {


    private static final Logger log = LoggerFactory.getLogger(RRMybatisBootstrap.class)

    @Override
    public void init(@Nonnull String sessionFactoryName, @Nonnull SqlSession session) {
        // operations after first connection to datasource
        log.debug("Setting the RR Schema")
        Connection conn = session.getConnection()
        try {
            PreparedStatement prep = conn.prepareStatement(
                    "select Schema_name from INFORMATION_SCHEMA.SCHEMATA where SCHEMA_NAME = 'RRDEC'")
            ResultSet rs = prep.executeQuery()
            if (!rs.next()) {
                log.debug("schema was not found -- creating")
                prep = conn.prepareStatement("create schema RRDEC")
                prep.execute()
            }
            prep = conn.prepareStatement(
                    "select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = 'RRDEC'"
            )
            rs = prep.executeQuery()
            if (!rs.next()) {
                log.debug("tables not found -- creating")
                CreateTables createTables = CreateTables.getInstance()
                createTables.createTables(conn)
            }

        } finally {
            //conn.close()
            log.debug("connection now closed")
        }

    }

    @Override
    public void destroy(@Nonnull String sessionFactoryName, @Nonnull SqlSession session) {
        // operations before disconnecting from the datasource
    }

}
