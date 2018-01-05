/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-08 20:52
 */
package com.lixianling.rabbit.dao.redis;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: RedisExistException.java 40 2016-01-08 17:11:07Z hank $
 */
public class RedisExistException extends RedisException {

    public RedisExistException(String reason) {
        super(reason);
    }
}
