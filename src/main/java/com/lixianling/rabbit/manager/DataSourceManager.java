/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:17
 */
package com.lixianling.rabbit.manager;

import com.lixianling.rabbit.conf.DataSourceConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton pattern
 *
 * @author Xianling Li(hanklee)
 * $Id: DataSourceManager.java 39 2016-01-08 12:04:37Z hank $
 */
public final class DataSourceManager {
    private static final DataSourceManager INSTANCE = new DataSourceManager();

    private Map<String, DataSource> dataSources = new ConcurrentHashMap<String, DataSource>(10);

    private DataSource dataSource = null;
    private String defaultName = null;

    private DataSourceManager() {
        init(RabbitManager.RABBIT_CONFIG.dataSources);
    }

    protected static void register() {
        // nothing to do
        for (String key : INSTANCE.dataSources.keySet()) {
            Connection conn = null;
            ResultSet rs = null;
            QueryRunner queryRunner = getQueryRunner(key);
            try {
                try {
                    conn = queryRunner.getDataSource().getConnection();
                    DatabaseMetaData md = conn.getMetaData();
                    rs = md.getTables(null, null, "%", null);
                    while (rs.next()) {
                        String table_name = rs.getString(3);
                        DBObjectManager.registerMySQLTable(key, table_name);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (conn != null) {
                        DbUtils.close(conn);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static DataSourceManager getInstance() {
        return INSTANCE;
    }

    /**
     *
     *
     */
    private synchronized void init(Map<String, DataSourceConfig> _dataSourceConf) {
        for (String name : _dataSourceConf.keySet()) {
            DataSourceConfig dataSourceConf = _dataSourceConf.get(name);
            try {
                // setup the connection pool
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(dataSourceConf.url);
                config.setUsername(dataSourceConf.user);
                config.setPassword(dataSourceConf.password);
                config.setDriverClassName(dataSourceConf.driver);
//                config.setDataSourceClassName(dataSourceConf.driver);
                config.setConnectionTestQuery("SELECT 1");
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                HikariDataSource ds = new HikariDataSource(config);
//                BoneCPDataSource boneCPDataSource = new BoneCPDataSource();
//                boneCPDataSource.setDriverClass(dataSourceConf.driver);
//                boneCPDataSource.setJdbcUrl(dataSourceConf.url); //
//                boneCPDataSource.setUsername(dataSourceConf.user);
//                boneCPDataSource.setPassword(dataSourceConf.password);
////                boneCPDataSource.setIdleConnectionTestPeriod(300);
//                boneCPDataSource.setMinConnectionsPerPartition(5);
//                boneCPDataSource.setMaxConnectionsPerPartition(15);
////            boneCPDataSource.setPartitionCount(1);
//                boneCPDataSource.setPartitionCount(3);
//                boneCPDataSource.setAcquireIncrement(5);
//
////                boneCPDataSource.setIdleMaxAge(240);
//                boneCPDataSource.setIdleMaxAge(0, TimeUnit.MILLISECONDS);
//                boneCPDataSource.setConnectionTimeoutInMs(10000); // process time out
//                boneCPDataSource.setIdleConnectionTestPeriodInMinutes(10);
//                boneCPDataSource.setConnectionTestStatement("/* ping */ SELECT 1");
                if (dataSourceConf._default) {
                    dataSource = ds;
                    defaultName = name;
                }
                dataSources.put(name, ds);
            } catch (Exception e) {
                e.printStackTrace();
            }

//            if (dataSource != null) {
//                DBOperationHelper.setInnerRunner(new QueryRunner(dataSource));
//                DBBatchOperationHelper.setInnerRunner(new QueryRunner(dataSource));
//            }
        }
    }


    public QueryRunner newQueryRunner() {
        return new QueryRunner(dataSource);
    }

    public QueryRunner newQueryRunner(String name) {
        DataSource _dataSource = dataSources.get(name);
        if (_dataSource != null) {
            return new QueryRunner(_dataSource);
        }
        return null;
    }

    public static QueryRunner getQueryRunner() {
        return INSTANCE.newQueryRunner();
    }

    public static Connection getConnection() throws SQLException {
        return INSTANCE.dataSource.getConnection();
    }

    public static Connection getConnection(String name) throws SQLException {
        DataSource ds = getDataSource(name);
        if (ds != null) {
            return ds.getConnection();
        }
        return null;
    }

    public static DataSource getDataSource() {
        return INSTANCE.dataSource;
    }

    public static String getDefaultName() {
        return INSTANCE.defaultName;
    }

    public static DataSource getDataSource(String name) {
        return INSTANCE.dataSources.get(name);
    }

    /**
     * synchronize this method.
     *
     * @return QueryRunner
     */
    public static QueryRunner getQueryRunner(final String name) {
        return INSTANCE.newQueryRunner(name);
    }
}
