package com.lixianling.rabbit.dao;

import com.lixianling.rabbit.DBException;

/**
 * @author hank
 */
public interface DAOHandler<T> {
    T handle(Object con) throws DBException;
}
