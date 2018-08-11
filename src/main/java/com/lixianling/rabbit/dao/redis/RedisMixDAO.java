/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 23:16
 */
package com.lixianling.rabbit.dao.redis;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAOHandler;
import com.lixianling.rabbit.dao.sql.SQLDAO;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.MixDAO;
import com.lixianling.rabbit.manager.RedisManager;
import org.apache.commons.dbutils.QueryRunner;

import java.util.Collection;
import java.util.List;

/**
 * @author Xianling Li(hanklee)
 * $Id: RedisMixDAO.java 39 2016-01-08 12:04:37Z hank $
 */
public class RedisMixDAO extends MixDAO {

    private DAO dao;
    private DAO backDao;

    public RedisMixDAO(String source) {
        super(source);
        dao = new RedisDAO(RedisManager.getPool());
        backDao = new SQLDAO(source);
    }

    public RedisMixDAO(String source, String dbsource) {
        super(source);
        dao = new RedisDAO(RedisManager.getPool());
        backDao = new SQLDAO(dbsource);
    }

    @Override
    public void update(DBObject obj, String table) throws DBException {
        this.dao.update(obj, table);
    }

    @Override
    public void delete(DBObject obj, String table) throws DBException {
        this.dao.delete(obj, table);
        this.backDao.delete(obj, table);
    }

    @Override
    public void insert(DBObject obj, String table) throws DBException {
        this.dao.insert(obj, table);
        this.backDao.insert(obj, table);
    }

    @Override
    public <T extends DBObject> T getObject(String table, String[] fields, Object... objs) throws DBException {
        return null;
    }

    @Override
    public DBObject getObject(final String table, final Object... objs) throws DBException {
        return this.dao.getObject(table, objs);
    }

    @Override
    public <T extends DBObject> List<T> getObjects(String table, String[] fields, Object... objs) throws DBException {
        return null;
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
    public <T> T execute(DAOHandler<T> daoHandler) throws DBException {
        return this.dao.execute(daoHandler);
    }

    @Override
    public void syncObject(String table, Object... objs) throws DBException {
        DBObject nobj = this.dao.getObject(table, objs);
        this.backDao.update(nobj, table);
    }

    @Override
    public void syncTable(String table) throws DBException {
        // FIXME not implements
    }
}
