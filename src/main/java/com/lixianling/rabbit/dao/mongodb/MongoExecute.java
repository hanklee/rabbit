/**
 * Create time: 11-Jan-2018
 */
package com.lixianling.rabbit.dao.mongodb;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.dao.DAOExecute;
import com.mongodb.MongoClient;
import org.elasticsearch.client.transport.TransportClient;

/**
 * @author hank
 */
public abstract class MongoExecute<T> implements DAOExecute<T> {
    private MongoClient client;

    public MongoExecute(MongoClient client) {
        this.client = client;
    }

    public T run() throws DBException {
        return execute(this.client);
    }
}
