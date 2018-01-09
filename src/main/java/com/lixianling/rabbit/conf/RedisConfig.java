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
    public boolean cluster = false;

    public static class Host {
        public String host;
        public int port;
        public int index;
        public String password;
    }

}
