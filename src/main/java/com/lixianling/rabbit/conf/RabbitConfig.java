/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:09
 */
package com.lixianling.rabbit.conf;

import java.util.Map;

/**
 * @author Xianling Li(hanklee)
 *         $Id: RabbitConfig.java 38 2016-01-07 17:07:06Z hank $
 */
public final class RabbitConfig {
    public enum Mode {
        MYSQL,REDIS,MIX
    }
    public final static String DEFAULT_CACHE_KEY_FIELD = "system";
    public CacheConfig cacheConfig;
    public Map<String, DataSourceConf> dataSources;
    public RedisConfig redisConfig;
    public Mode mode;
}
