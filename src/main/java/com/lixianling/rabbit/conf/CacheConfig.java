/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:11
 */
package com.lixianling.rabbit.conf;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: CacheConfig.java 41 2016-01-09 17:39:32Z hank $
 */
public class CacheConfig {
    public String cachePrefix;
    public long syncTime;
    public String cache;
    public boolean memory_cache;
    public Class<?> afterCacheClass;
    public RedisConfig redisConfig;

    public static final CacheConfig DEFAULT;

    static {
        DEFAULT = new CacheConfig();
        DEFAULT.cachePrefix = "c_";
        DEFAULT.syncTime = 900000; // 15min
        DEFAULT.cache = "default";
        DEFAULT.memory_cache = true;
        DEFAULT.redisConfig = null;
//        DEFAULT.redisConfig.hosts = new ArrayList<RedisConfig.Host>();
    }
}
