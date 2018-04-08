/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:17
 */
package com.lixianling.rabbit.manager;

import com.lixianling.rabbit.conf.DataSourceConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton pattern
 *
 * @author Xianling Li(hanklee)
 * $Id: DataSourceManager.java 39 2016-01-08 12:04:37Z hank $
 */
public final class DataSourceManager {
    private static final DataSourceManager INSTANCE;

    static {
        INSTANCE = new DataSourceManager();
    }

    private Map<String, DataSource> dataSources = new ConcurrentHashMap<String, DataSource>(10);

    private DataSource dataSource = null;

    private DataSourceManager() {
        init(RabbitManager.RABBIT_CONFIG.dataSources);
    }

    protected static void register(){
        // nothing to do
    }

    public static DataSourceManager getInstance(){
        return INSTANCE;
    }

    /**
     *
     *
     */
    public void init(Map<String, DataSourceConfig> _dataSourceConf) {
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
                if (dataSourceConf._default)
                    dataSource = ds;
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

    public static DataSource getDataSource() {
        return INSTANCE.dataSource;
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
        // dbcp
        // if (DBDataSourceHelper.dataSource == null) {
        // BasicDataSource dbcpDataSource = new BasicDataSource();
        // dbcpDataSource
        // .setUrl("jdbc:mysql://localhost:3306/game?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull");
        // dbcpDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        // dbcpDataSource.setUsername("root");
        // dbcpDataSource.setPassword("hank");
        // dbcpDataSource.setDefaultAutoCommit(true);
        // dbcpDataSource.setMaxActive(100);
        // dbcpDataSource.setMaxIdle(30);
        // dbcpDataSource.setMaxWait(500);
        //
        // DBDataSourceHelper.dataSource = (DataSource) dbcpDataSource;
        // System.out.println("Initialize dbcp...");
        // }
//        Properties p = FileHelper.getProperties(fileName);

//        try {
//            ComboPooledDataSource c3p0DataSource = new ComboPooledDataSource();
//            c3p0DataSource.setDriverClass(p.getProperty("game.jdbc.driverClassName", DRIVER));
//            c3p0DataSource.setJdbcUrl(p.getProperty("game.jdbc.url", DB_URL));
//            c3p0DataSource.setUser(p.getProperty("game.jdbc.user", USER_NAME));
//            c3p0DataSource.setPassword(p.getProperty("game.jdbc.password", USER_PASS));
//            //c3p0DataSource.setMaxIdleTime(10); // default is 0 seconds, 0
//            // means connections never expire
//            c3p0DataSource.setTestConnectionOnCheckin(true);
//            c3p0DataSource.setMinPoolSize(3);
//            c3p0DataSource.setMaxPoolSize(20);
//            c3p0DataSource.setMaxIdleTime(15000);
////            c3p0DataSource.setMaxStatements(100);  // this is important
//            c3p0DataSource.setAcquireIncrement(5);
////            c3p0DataSource.setAutoCommitOnClose(true);
//            c3p0DataSource.setIdleConnectionTestPeriod(300);
////            c3p0DataSource.setMaxPoolSize(20); // default is 15
//            dataSource = c3p0DataSource;

        // setup the connection pool
//            BoneCPDataSource boneCPDataSource = new BoneCPDataSource();
//            boneCPDataSource.setDriverClass(p.getProperty("game.jdbc.driverClassName", DRIVER));
//            boneCPDataSource.setJdbcUrl(p.getProperty("game.jdbc.url", USER_DB_DB_URL)); //
//            boneCPDataSource.setUsername(p.getProperty("game.jdbc.user", USER_NAME));
//            boneCPDataSource.setPassword(p.getProperty("game.jdbc.password", USER_PASS));
//            boneCPDataSource.setIdleConnectionTestPeriod(300);
//            boneCPDataSource.setMinConnectionsPerPartition(5);
//            boneCPDataSource.setMaxConnectionsPerPartition(15);
////            boneCPDataSource.setPartitionCount(1);
//            boneCPDataSource.setPartitionCount(3);
//            boneCPDataSource.setAcquireIncrement(5);
//            boneCPDataSource.setIdleMaxAge(240);
//            boneCPDataSource.setConnectionTimeoutInMs(1000);
//            dataSource = boneCPDataSource;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return new QueryRunner(dataSource);
    }
}
