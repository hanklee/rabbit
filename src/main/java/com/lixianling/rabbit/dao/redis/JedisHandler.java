/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-07 21:20
 */
package com.lixianling.rabbit.dao.redis;

import redis.clients.jedis.Jedis;

/**
 * @author Xianling Li(hanklee)
 *         $Id: JedisHandler.java 39 2016-01-08 12:04:37Z hank $
 */
public interface JedisHandler<T> {

    T handle(Jedis connection) throws RedisException;
}
