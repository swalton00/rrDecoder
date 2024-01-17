package com.spw.rr

import griffon.plugins.mybatis.MybatisBootstrap
import org.apache.ibatis.session.SqlSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Nonnull
import javax.inject.Named
import java.sql.Connection
import java.sql.PreparedStatement

@Named("myBootstrap")
class RRMybatisBootstrap implements MybatisBootstrap {


    private static final Logger log = LoggerFactory.getLogger(RRMybatisBootstrap.class)

    @Override
    public void init(@Nonnull String sessionFactoryName, @Nonnull SqlSession session) {
        // operations after first connection to datasource
        log.debug("Setting the RR Schema")
        Connection conn = session.getConnection()
        try {
            PreparedStatement prep = conn.prepareStatement("SET schema RR")
            //prep.execute()
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
