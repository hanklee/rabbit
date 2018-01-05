/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 23:16
 */
package com.lixianling.rabbit.dao.sql;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.manager.CacheManager;
import com.lixianling.rabbit.dao.CacheDAO;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.MixDAO;
import org.apache.commons.dbutils.QueryRunner;

import java.util.Collection;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: SQLMixDAO.java 39 2016-01-08 12:04:37Z hank $
 */
public class SQLMixDAO extends MixDAO {

    protected CacheDAO cacheDao; // cache operation interface
    private DAO dao;

    public SQLMixDAO(final QueryRunner queryRunner) {
        cacheDao = CacheManager.getCacheDAO(queryRunner);
        dao = new SQLDAO(queryRunner);
    }


    public void insertCacheObject(DBObject obj, String table) throws DBException {
        obj.generateId(table, idGen);
        cacheDao.insert(obj, table);
        _insertCacheObject(obj, table);
    }

    public void deleteCacheObject(DBObject obj, String table) throws DBException {
        cacheDao.delete(obj, table);
        _deleteCacheObject(obj, table);
    }

    public void updateCacheObject(DBObject obj, String table) throws DBException {
        cacheDao.update(obj, table);
        _updateCacheObject(obj, table);
    }

    public final void insertCacheIncreaseByIdObject(String table, DBObject obj) throws DBException {
        // directly insert to sql server
        insert(obj, table);
        _insertCacheObject(obj, table);
    }

    public final void cleanCacheObject(String table, String _key) throws DBException {
        cacheDao.cleanDataToDB(table, _key, this);
    }

    public final void cleanCacheObject(String table) throws DBException {
        cacheDao.cleanDataToDB(table, this);
    }

    public final void cleanCacheObjectByKey(String key) throws DBException {
        cacheDao.cleanDataToDBByKey(key, this);
    }


    public final void cleanCacheObjectNoSync(String table, String _key) throws DBException {
        cacheDao.cleanData(table, _key);
    }

    public final void cleanCacheObjectNoSync(String table) throws DBException {
        cacheDao.cleanData(table);
    }

    public final void cleanCacheObjectByKeyNoSync(String key) throws DBException {
        cacheDao.cleanDataByKey(key);
    }

    @Override
    public void update(DBObject obj, String table) throws DBException {
        this.dao.update(obj, table);
    }

    @Override
    public void delete(DBObject obj, String table) throws DBException {
        this.dao.delete(obj, table);
    }

    @Override
    public void insert(DBObject obj, String table) throws DBException {
        this.dao.insert(obj, table);
    }

    @Override
    public void update(Collection<? extends DBObject> objs, String table_name) throws DBException {
        this.dao.update(objs, table_name);
    }

    @Override
    public void insert(Collection<? extends DBObject> objs, String table_name) throws DBException {
        this.dao.insert(objs, table_name);
    }

    @Override
    public void delete(Collection<? extends DBObject> objs, String table_name) throws DBException {
        this.dao.delete(objs, table_name);
    }
}
