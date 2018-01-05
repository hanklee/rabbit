/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 23:04
 */
package com.lixianling.rabbit.dao.redis;

import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.MultiKeyCommands;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: MixJedisCommands.java 41 2016-01-09 17:39:32Z hank $
 */
public interface MixJedisCommands extends JedisCommands,MultiKeyCommands {
}
