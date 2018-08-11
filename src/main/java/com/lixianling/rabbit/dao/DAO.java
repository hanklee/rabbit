/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:23
 */
package com.lixianling.rabbit.dao;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.IdGenerator;

import java.util.Collection;
import java.util.List;

/**
 * @author Xianling Li(hanklee)
 * $Id: DAO.java 38 2016-01-07 17:07:06Z hank $
 */
public abstract class DAO {

    protected IdGenerator idGen;
    protected final String source;

    public DAO(String source) {
        this.source = source;
    }

    public IdGenerator getIdGen() {
        return idGen;
    }

    public void setIdGen(IdGenerator idGen) {
        this.idGen = idGen;
    }

    public String getSource() {
        return source;
    }

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

    /**
     * get object through table key value
     *
     * @param table table name
     * @param objs  key value
     * @param <T>   return Object type
     * @return a object
     * @throws DBException db exception
     */
    public abstract <T extends DBObject> T getObject(String table, Object... objs) throws DBException;

    /**
     * get object through table fields and their value
     *
     * @param table  table name
     * @param fields fields name
     * @param objs   key value
     * @param <T>    return Object type
     * @return a object
     * @throws DBException db exception
     */
    public abstract <T extends DBObject> T getObject(String table, String[] fields, Object... objs) throws DBException;

    /**
     * get list of object through table fields and their value
     *
     * @param table  table name
     * @param fields fields name
     * @param objs   key value
     * @param <T>    return Object type
     * @return a object
     * @throws DBException db exception
     */
    public abstract <T extends DBObject> List<T> getObjects(String table, String[] fields, Object... objs) throws DBException;

    /**
     *
     *  delete objects
     *
     * @param table table name
     * @param fields fields name
     * @param objs fields value
     * @throws DBException db exception
     */
    public abstract void deleteObjects(String table, String[] fields, Object... objs) throws DBException;


    /*

      BATCH OPERATION

     */

    public abstract void update(Collection<? extends DBObject> objs, String table_name) throws DBException;

    public abstract void insert(Collection<? extends DBObject> objs, String table_name) throws DBException;

    public abstract void delete(Collection<? extends DBObject> objs, String table_name) throws DBException;

    public abstract <T> T execute(DAOHandler<T> daoHandler) throws DBException;
}
