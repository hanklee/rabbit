package com.lixianling.rabbit.dao.sql;

import com.lixianling.rabbit.DBException;

import java.sql.Connection;

/**
 * @author Xianling Li
 */
public interface SQLDAOTransitionHandler<T> {
    T handle(Connection conn) throws DBException;
}
