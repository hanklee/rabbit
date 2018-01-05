/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:18
 */
package com.lixianling.rabbit.manager;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.cache.AfterCleanCache;
import com.lixianling.rabbit.conf.CacheConfig;
import com.lixianling.rabbit.conf.RabbitConfig;
import com.lixianling.rabbit.dao.CacheDAO;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.redis.RedisCacheDAO;
import com.lixianling.rabbit.dao.sql.SQLNOCacheDAO;
import org.apache.commons.dbutils.QueryRunner;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * table_name
 * <p/>
 * table_name_key_list -> [key1,key2,key3....]
 * <p/>
 * table_name_insert_key(n) -> [Object1,Object2,Object3....]
 * table_name_delete_key(n) -> [Object1,Object2,Object3....]
 * table_name_update_key(n) -> [Object1,Object2,Object3....]
 * <p/>
 *
 * @author Xianling Li(hanklee)
 *         $Id: CacheManager.java 39 2016-01-08 12:04:37Z hank $
 */
public final class CacheManager {

    public static long SYNC_DATABASE_TIME = 900000; // 15 min

    public static final String CACHE_SUFFIX;
    public static final String REDIS_CACHE_NAME = "redis";

    public static final String INSERT_CACHE_OP = "_ins_";
    public static final String UPDATE_CACHE_OP = "_upd_";
    public static final String DELETE_CACHE_OP = "_del_";


    private static String CACHE_NAME;

    //AfterCleanKeyCache 一个清除数据库操作内存之后执行的接口
    private static AfterCleanCache afterCleanCache = null;

    private static Map<String, Long> keys;


    private static CacheDAO cacheDAO;
    private static DAO dao;

    private CacheManager() {
    }

    static {
        CACHE_SUFFIX = RabbitManager.RABBIT_CONFIG.cacheConfig.cachePrefix;
        init(RabbitManager.RABBIT_CONFIG);
    }

    protected static void register(){
        // nothing to do
    }

    /**
     * must initialization
     */
    private static void init(final RabbitConfig rabbitConfig) {
        CacheConfig config = rabbitConfig.cacheConfig;
        CACHE_NAME = config.cache;
        // 10 sec to execute once

        SYNC_DATABASE_TIME = config.syncTime;
        try {
            if (rabbitConfig.cacheConfig.afterCacheClass != null)
                afterCleanCache = (AfterCleanCache) rabbitConfig.cacheConfig.afterCacheClass.newInstance();
        } catch (Exception ignored) {
        }
        keys = new ConcurrentHashMap<String, Long>();
        if (CACHE_NAME.equals(REDIS_CACHE_NAME)) {
            cacheDAO = new RedisCacheDAO();
        } else {
            cacheDAO = new SQLNOCacheDAO(DataSourceManager.getQueryRunner());
        }
        dao = new SQLNOCacheDAO(DataSourceManager.getQueryRunner());

        // load cache to db
        try {
            cacheDAO.cleanDataToDB("*", dao);
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    public static CacheDAO getCacheDAO(QueryRunner queryRunner) {
        if (CACHE_NAME.equals(REDIS_CACHE_NAME)) {
            return new RedisCacheDAO();
        } else {
            // nothing save to cache
            return new SQLNOCacheDAO(queryRunner);
        }
    }

    /*

            C A C H E    TO     D A T A B A S E

     */

    public static void refresh_cache_by_syn_time() {
        long curTime = System.currentTimeMillis();
        for (String key : keys.keySet()) {
            Long myTime = keys.get(key);
            if ((curTime - myTime) > SYNC_DATABASE_TIME) {
                update_cache_to_database_by_key(key);
                keys.put(key, curTime);
            }
        }
    }

    public static synchronized void update_cache_to_database_by_key(final String key) {
        long cTime = System.currentTimeMillis();
        try {
            cacheDAO.cleanDataToDBByKey(key, dao);
        } catch (DBException e) {
            e.printStackTrace();
        }

        System.err.println("cache sync to database,key: " + key + ", time(ms):"
                + (System.currentTimeMillis() - cTime));
    }

    public static void update_remove_cache_to_database_by_key(final String key) {
        keys.remove(key);
        update_cache_to_database_by_key(key);
        if (afterCleanCache != null) {
            afterCleanCache.cleanKeyCache(key);
        }
    }

    public static void register_key(final String key) {
        keys.put(key, System.currentTimeMillis());
    }
}
