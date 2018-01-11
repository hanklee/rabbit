package com.lixianling.rabbit.dao;

import com.lixianling.rabbit.DBException;
import org.elasticsearch.client.transport.TransportClient;

/**
 * @author hank
 */
public interface DAOHandler<T> {
    T handle(Object con) throws DBException;
}
