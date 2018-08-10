package com.lixianling.rabbit.dao;

import com.lixianling.rabbit.DBException;

/**
 * @author hank
 */
public interface DAOExecute<T> {
    T execute(Object con) throws DBException;
    T run() throws DBException;
}
