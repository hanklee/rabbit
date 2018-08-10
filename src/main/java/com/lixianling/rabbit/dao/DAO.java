/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:23
 */
package com.lixianling.rabbit.dao;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.IdGenerator;

import java.util.Collection;

/**
 * @author Xianling Li(hanklee)
 * $Id: DAO.java 38 2016-01-07 17:07:06Z hank $
 */
public abstract class DAO {

    protected IdGenerator idGen;

    public void setIdGenerator(IdGenerator idGen) {
        this.idGen = idGen;
    }

    public void update(DBObject obj) throws DBException {
        update(obj, obj.getTableName());
    }

    public void insert(DBObject obj) throws DBException {
        insert(obj, obj.getTableName());
    }

    public void delete(DBObject obj) throws DBException {
        delete(obj, obj.getTableName());
    }

    /*
      abstract method
    */

    public abstract void update(DBObject obj, String table) throws DBException;

    public abstract void delete(DBObject obj, String table) throws DBException;

    public abstract void insert(DBObject obj, String table) throws DBException;

    public abstract DBObject getObject(String table, Object... objs) throws DBException;

    /*

      BATCH OPERATION

     */

    public abstract void update(Collection<? extends DBObject> objs, String table_name) throws DBException;

    public abstract void insert(Collection<? extends DBObject> objs, String table_name) throws DBException;

    public abstract void delete(Collection<? extends DBObject> objs, String table_name) throws DBException;

    public abstract <T> T execute(DAOHandler<T> daoHandler) throws DBException;
}
