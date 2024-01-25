package com.spw.rr

import com.spw.rr.database.CreateTables
import griffon.plugins.mybatis.MybatisBootstrap
import org.apache.ibatis.session.SqlSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Nonnull
import javax.inject.Named
import javax.swing.JOptionPane
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

@Named("myBootstrap")
class RRMybatisBootstrap implements MybatisBootstrap {


    private static final Logger log = LoggerFactory.getLogger(RRMybatisBootstrap.class)

    @Override
    public void init(@Nonnull String sessionFactoryName, @Nonnull SqlSession session) {
        // operations after first connection to datasource
        log.debug("checking for the existence of the tables")
        Connection conn = session.getConnection()
        try {
            boolean h2DB = false
            boolean db2db = false
            try {
                log.debug("trying an H2 statement")
                PreparedStatement prep = conn.prepareStatement(
                        "select Schema_name from INFORMATION_SCHEMA.SCHEMATA where SCHEMA_NAME = 'RRDEC'")
                ResultSet rs = prep.executeQuery()
                h2DB = true
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
            } catch (Exception h2ex) {
                log.warn("h2 didn't work -- will try DB2", h2ex)
            }
            if (!h2DB) {
                log.debug("trying DB2 connection now")
                try {
                    PreparedStatement prep = conn.prepareStatement(
                            "select NAME from SYSIBM.SYSTABLES where NAME = 'RRDEC'")
                    ResultSet rs = prep.executeQuery()
                    log.debug("DB2 statement worked")
                    db2db = true
                    if (!rs.next()) {
                        log.error("schema was not found -- needs to be created")
                        int retValue = JOptionPane.showConfirmDialog(null,
                                "Press OKAY to close -- DB2 requires that tables be already created",
                                "RrDecoder Restart message",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.WARNING_MESSAGE)
                        if (retValue == JOptionPane.OK_OPTION ) {
                            application.shutdown()
                        }
                    }
                } catch (Exception db2ex) {
                    log.warn("DB2 connection did not work either")
                }
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
