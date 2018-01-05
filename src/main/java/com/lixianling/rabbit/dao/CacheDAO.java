/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:24
 */
package com.lixianling.rabbit.dao;

import com.lixianling.rabbit.DBException;

/**
 * @author Xianling Li(hanklee)
 *         $Id: CacheDAO.java 38 2016-01-07 17:07:06Z hank $
 */
public abstract class CacheDAO extends DAO {

    // just for back up dao operation

    public abstract void cleanData(String table, String key) throws DBException;

    public abstract void cleanData(String table) throws DBException;

    public abstract void cleanDataByKey(String key) throws DBException;

    public abstract void cleanDataToDB(String table, String myKey, DAO dao) throws DBException;

    public abstract void cleanDataToDB(String table, DAO dao) throws DBException;

    public abstract void cleanDataToDBByKey(String key, DAO dao) throws DBException;

}
