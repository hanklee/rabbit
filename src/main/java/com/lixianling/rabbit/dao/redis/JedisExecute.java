/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 23:01
 */
package com.lixianling.rabbit.dao.redis;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.dao.DAOExecute;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: JedisExecute.java 41 2016-01-09 17:39:32Z hank $
 */
public abstract class JedisExecute<T> implements DAOExecute<T> {
    private JedisPool pool;

    public JedisExecute(JedisPool pool) {
        this.pool = pool;
    }

//    public abstract T execute(Jedis connection) throws RedisException;

    public T run() throws DBException {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return execute(jedis);
        } catch (DBException e) {
            //释放redis对象
//            pool.returnBrokenResource(jedis);
//            pool.returnResource(jedis);
            throw e;
        } catch (Exception e) {
            //释放redis对象
//            pool.returnBrokenResource(jedis);
            e.printStackTrace();
            throw new DBException(e.getMessage());
        } finally {
            //返还到连接池
//            returnResource(pool, jedis);
            pool.returnResource(jedis);
        }
    }
}
