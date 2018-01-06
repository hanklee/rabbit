/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-07 02:38
 */
package com.lixianling.rabbit.dao.redis;

import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.manager.DBObjectManager;

import java.lang.reflect.Field;

/**
 * reference : http://redis.io/topics/twitter-clone
 * <p/>
 * 1: get key id : next_$table_name_id
 * <p/>
 * 2: hmset $table_name:$key_id ....
 * <p/>
 * 3: if object has unique value , set $table_name, $unique_value, $key_id
 *
 * @author Xianling Li(hanklee)
 *         $Id: RedisObject.java 39 2016-01-08 12:04:37Z hank $
 */
public abstract class RedisObject extends DBObject {



}
