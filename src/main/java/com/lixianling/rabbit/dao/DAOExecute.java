package com.lixianling.rabbit.dao;

import com.lixianling.rabbit.DBException;
import org.elasticsearch.client.transport.TransportClient;

/**
 * @author hank
 */
public interface DAOExecute<T> {
    T execute(Object con) throws DBException;
    T run() throws DBException;
}
