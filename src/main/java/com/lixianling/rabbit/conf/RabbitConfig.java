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
    // public enum Mode {
    //     MYSQL,REDIS,MIX
    // }

    public Map<String, DataSourceConfig> dataSources;
    public RedisConfig redisConfig;
    public String mode;
    public String sources;
    public DBObjectConfig dbObjectConfig;
    public ElasticConfig elasticConfig;
    public MongoConfig mongoConfig;
    public TableConfig jsonTableConfig;
}
