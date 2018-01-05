/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 23:12
 */
package com.lixianling.rabbit.dao;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;

/**
 *
 * cache dao and real operation dao mix together
 *
 * @author Xianling Li(hanklee)
 *         $Id: MixDAO.java 38 2016-01-07 17:07:06Z hank $
 */
public abstract class MixDAO extends DAO {
    // cache operation

    /**
     * 3 method , according you requirement to rewrite
     */
    public void _insertCacheObject(DBObject obj, String table) throws DBException {
    }

    public void _updateCacheObject(DBObject obj, String table) throws DBException {
    }

    public void _deleteCacheObject(DBObject obj, String table) throws DBException {
    }

    /**
     * insert to cache
     * @param obj
     * @throws DBException
     */
    public void insertCacheObject(DBObject obj) throws DBException {
        insertCacheObject(obj, obj.getTableName());
    }

    public void deleteCacheObject(DBObject obj) throws DBException {
        deleteCacheObject(obj, obj.getTableName());
    }

    public void updateCacheObject(DBObject obj) throws DBException {
        updateCacheObject(obj, obj.getTableName());
    }

    /*
      abstract method
    */

    public abstract void insertCacheObject(DBObject obj, String table) throws DBException;

    public abstract void deleteCacheObject(DBObject obj, String table) throws DBException;

    public abstract void updateCacheObject(DBObject obj, String table) throws DBException;


    // clean cache to db
    public abstract void cleanCacheObject(String table, String _key) throws DBException;

    public abstract void cleanCacheObject(String table) throws DBException;

    public abstract void cleanCacheObjectByKey(String key) throws DBException;


    // just clean cache data

    public abstract void cleanCacheObjectNoSync(String table, String _key) throws DBException;

    public abstract void cleanCacheObjectNoSync(String table) throws DBException;

    public abstract void cleanCacheObjectByKeyNoSync(String key) throws DBException;
}
