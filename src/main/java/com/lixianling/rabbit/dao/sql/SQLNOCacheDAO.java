/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 22:45
 */
package com.lixianling.rabbit.dao.sql;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.CacheDAO;
import com.lixianling.rabbit.dao.DAO;
import org.apache.commons.dbutils.QueryRunner;

import java.util.Collection;

/**
 * @author Xianling Li(hanklee)
 *         $Id: SQLNOCacheDAO.java 38 2016-01-07 17:07:06Z hank $
 */
public class SQLNOCacheDAO extends CacheDAO {

    private DAO dao;

    public SQLNOCacheDAO(final QueryRunner queryRunner) {
        this.dao = new SQLDAO(queryRunner);
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


    @Override
    public void cleanData(String table, String key) throws DBException {
        // no implement
    }

    @Override
    public void cleanData(String table) throws DBException {
        // no implement
    }

    @Override
    public void cleanDataByKey(String table) throws DBException {
        // no implement
    }

    @Override
    public void cleanDataToDB(String table, String myKey, DAO dao) throws DBException {
        // no implement
    }

    @Override
    public void cleanDataToDB(String table, DAO dao) throws DBException {
        // no implement
    }

    @Override
    public void cleanDataToDBByKey(String key, DAO dao) throws DBException {
        // no implement
    }
}
