/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-07 22:53
 */
package com.lixianling.rabbit.conf;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: RedisConfig.java 40 2016-01-08 17:11:07Z hank $
 */
public class RedisConfig {
    public List<Host> hosts;
    public String password;
    public boolean cluster;

    public static class Host {
        public String host;
        public int port;
    }

    /**
     *  redis object configure
     */
    public Map<String, String> tableToClass;
    public Map<String, String> classToTable;
    public Map<String, String> tableField;
    public Map<String, String> tableUniqueField;
    public Map<String, String> tableIncrField;
    public Map<String, String> tableKeyField;
}
