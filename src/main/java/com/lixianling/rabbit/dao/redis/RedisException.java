/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-08 17:50
 */
package com.lixianling.rabbit.dao.redis;

import com.lixianling.rabbit.DBException;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: RedisException.java 39 2016-01-08 12:04:37Z hank $
 */
public class RedisException extends DBException {

    public RedisException(String reason) {
        super(reason);
    }

    public RedisException(String reason,int code) {
        super(reason,code);
    }

}
