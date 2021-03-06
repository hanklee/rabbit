/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-07 00:45
 */
package com.lixianling.rabbit.dao.redis;

import com.alibaba.fastjson.JSONObject;
import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.DAOHandler;
import com.lixianling.rabbit.manager.DBObjectManager;
import com.lixianling.rabbit.manager.RabbitManager;
import com.lixianling.rabbit.manager.RedisManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.lixianling.rabbit.DBException.CODE_EXIST_VALUE;
import static com.lixianling.rabbit.DBException.CODE_NOTFOUND;

//import org.json.JSONObject;

/**
 * if dbobject has key filed , redis will increase number store in next_xxx_id
 * all id number store in xxxx_ids
 * <p>
 * if not nothing process
 *
 * @author Xianling Li(hanklee)
 * $Id: RedisDAO.java 41 2016-01-09 17:39:32Z hank $
 */
public class RedisDAO extends DAO {

    private JedisPool pool;

    public final static String TABLE_IDS = ":ids:";
    public final static String TABLE_NEXT_ID = ":next_id:";
    public final static String TABLE_UNIQUE = ":unique:";

    public static String getTableIds(final DBObject obj, String source) {
        return obj.getTableName() + TABLE_IDS;
    }

    public static String getTableIds(final String table) {
        return table + TABLE_IDS;
    }

    public static String getTableNextId(final DBObject obj, String source) {
        return obj.getTableName() + TABLE_NEXT_ID;
    }

    public static String getTableNextId(final String table) {
        return table + TABLE_NEXT_ID;
    }


    public RedisDAO() {
        super(RedisManager.getSourcel());
        if (RabbitManager.RABBIT_CONFIG.mode.contains("redis")) {
            this.pool = RedisManager.getPool();
        } else {
            throw new RuntimeException("must config the redis in rabbit.xml file.");
        }
    }

    public RedisDAO(JedisPool pool) {
        super(RedisManager.getSourcel());
        this.pool = pool;
    }

    @Override
    public void update(final DBObject obj, final String table) throws DBException {
        new JedisExecute<Void>(pool) {
            public Void execute(Object con) throws DBException {
                Jedis connection = (Jedis) con;
                try {
                    String value = connection.get(obj.keyString(table));
                    if (value == null) {
                        throw new DBException("Not found data.", CODE_NOTFOUND);
                    }
                    obj.beforeUpdate(RedisDAO.this, table, connection);
                    connection.set(obj.keyString(table), obj.toDBJson(table).toString());
                    obj.afterUpdate(RedisDAO.this, table, connection);
                } catch (Exception e) {
                    throw new DBException(e.getMessage());
                }
                return null;
            }
        }.run();
    }

    @Override
    public void delete(final DBObject obj, final String table) throws DBException {
        new JedisExecute<Void>(pool) {
            public Void execute(Object con) throws DBException {
                Jedis connection = (Jedis) con;
                try {
                    obj.beforeDelete(RedisDAO.this, table, connection);
                    Pipeline pipeline = connection.pipelined();
                    pipeline.multi();
                    pipeline.del(obj.keyString(table));
//                        pipeline.srem(table + TABLE_IDS, obj.toKeyString(table));
                    pipeline.zrem(getTableIds(table), obj.keyString(table));
                    pipeline.exec();
                    obj.afterDelete(RedisDAO.this, table, connection);
                } catch (Exception e) {
                    throw new DBException(e.getMessage());
                }
                return null;
            }
        }.run();
    }

    @Override
    public void insert(final DBObject obj, final String table) throws DBException {
        new JedisExecute<Void>(pool) {
            @Override
            public Void execute(Object con) throws DBException {
                Jedis connection = (Jedis) con;
                try {
                    Long incrId = connection.incr(getTableNextId(table));
                    Pipeline pipeline = connection.pipelined();
                    Field keyField = DBObjectManager.getInsertIncrKeyField(source, table, obj);
                    //                    Transaction transaction = connection.multi();
                    if (keyField != null) {
                        if (keyField.getType().equals(Integer.TYPE)) {
                            keyField.set(obj, incrId.intValue());
                        } else if (keyField.getType().equals(Long.TYPE)) {
                            keyField.set(obj, incrId);
                        } else if (keyField.getType().equals(String.class)) {
                            keyField.set(obj, incrId.toString());
                        }

//                            pipeline.multi();
//                            pipeline.sadd(table + TABLE_IDS, obj.getKeyStringByRegisterKey(table));
                    } else {
                        String value = connection.get(obj.keyString(table));
                        if (value != null) {
                            throw new DBException("Has exist data.", CODE_EXIST_VALUE);
                        }
                    }
                    obj.beforeInsert(RedisDAO.this, table, connection);
                    pipeline.multi();
                    pipeline.zadd(table + TABLE_IDS, incrId, obj.keyString(table));
                    pipeline.set(obj.keyString(table), obj.toDBJson(table).toString());
                    pipeline.exec();
                    obj.afterInsert(RedisDAO.this, table, connection);
                } catch (DBException e) {
                    throw e;
                } catch (Exception e) {
                    throw new DBException(e.getMessage());
                }
                return null;
            }
        }.run();
    }

    @Override
    public void update(DBObject obj, String table, String[] fields) throws DBException {

    }

    @Override
    public void update(String table, Map<String, Object> valueObj, Map<String, Object> whereObj) throws DBException {

    }

    @Override
    public void update(String table, String[] fields, Object[] valueObjs, String[] whereFields, Object[] whereObjs) throws DBException {

    }

    @Override
    public <T extends DBObject> T getObject(String table, String[] fields, Object... objs) throws DBException {
        return null;
    }

    @Override
    public <T extends DBObject> T getObject(String table, Map<String, Object> whereObj) throws DBException {
        return null;
    }

    @Override
    public void update(Collection<? extends DBObject> objs, final String table_name) throws DBException {
        for (DBObject obj : objs) {
            update(obj, table_name);
        }
    }

    @Override
    public void insert(Collection<? extends DBObject> objs, String table_name) throws DBException {
        for (DBObject obj : objs) {
            insert(obj, table_name);
        }
    }

    @Override
    public void delete(Collection<? extends DBObject> objs, String table_name) throws DBException {
        for (DBObject obj : objs) {
            delete(obj, table_name);
        }
    }

    @Override
    public DBObject getObject(final String table, final Object... objs) throws DBException {
        return new JedisExecute<DBObject>(pool) {
            @Override
            public DBObject execute(Object con) throws DBException {
                Jedis connection = (Jedis) con;
                try {
                    Class<DBObject> objclazz = DBObjectManager.getClassByTable(source, table);
                    if (objclazz == null) {
                        throw new DBException("not found table class");
                    }
                    DBObject obj = null;
                    try {
                        obj = (DBObject) objclazz.newInstance();
                    } catch (Exception e) {
                        throw new DBException("wrong table class:" + objclazz.toString());
                    }
                    String keyString = table;
                    for (Object o : objs) {
                        keyString = keyString + ":" + String.valueOf(o);
                    }
                    String value = connection.get(keyString);
                    if (value == null)
                        throw new DBException("not found!");
                    return obj.cloneObj(JSONObject.parseObject(value));
                } catch (DBException e) {
                    throw e;
                }
            }
        }.run();

    }

    @Override
    public <T extends DBObject> List<T> getObjects(String table, String[] fields, Object... objs) throws DBException {
        return null;
    }

    @Override
    public <T extends DBObject> List<T> getObjects(String table, Map<String, Object> whereObj) throws DBException {
        return null;
    }

    @Override
    public void deleteObjects(String table, String[] fields, Object... objs) throws DBException {

    }

    @Override
    public void deleteObjects(String table, Map<String, Object> whereObj) throws DBException {

    }

    public <T> T execute(final DAOHandler<T> handler) throws DBException {
        return new JedisExecute<T>(pool) {
            @Override
            public T execute(Object con) throws DBException {
                return handler.handle(con);
            }
        }.run();
    }
}
