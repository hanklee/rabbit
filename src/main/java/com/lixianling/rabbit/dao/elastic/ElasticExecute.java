/**
 * Create time: 11-Jan-2018
 */
package com.lixianling.rabbit.dao.elastic;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.dao.DAOExecute;
import com.lixianling.rabbit.dao.redis.RedisException;
import org.elasticsearch.client.transport.TransportClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author hank
 */
public abstract class ElasticExecute<T> implements DAOExecute<T> {

    private TransportClient client;

    public ElasticExecute(TransportClient client) {
        this.client = client;
    }

    public T run() throws DBException {
        return execute(this.client);
    }
}
