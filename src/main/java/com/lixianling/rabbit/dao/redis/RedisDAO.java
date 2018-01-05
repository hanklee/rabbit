/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-07 00:45
 */
package com.lixianling.rabbit.dao.redis;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.manager.DBObjectManager;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * @author Xianling Li(hanklee)
 *         $Id: RedisDAO.java 41 2016-01-09 17:39:32Z hank $
 */
public class RedisDAO extends DAO {

    private JedisPool pool;

    public RedisDAO(JedisPool pool) {
        this.pool = pool;
    }

    @Override
    public void update(final DBObject obj, final String table) throws DBException {
        try {
            new JedisExecute<Void>(pool) {
                @Override
                public Void execute(Jedis connection) throws RedisException {
                    try {

                        String value = connection.get(obj.toKeyString(table));
                        if (value == null) {
                            throw new RedisException("Not found data.");
                        }

                        if (obj instanceof RedisObject) {
                            DBObject clone = obj.clone();
                            clone.JsonToObj(new JSONObject(value));
                            RedisObject robj = (RedisObject) obj;
                            RedisObject robj2 = (RedisObject) clone;
                            String uniqueValue = robj.uniqueValue();
                            if (uniqueValue != null
                                    && !uniqueValue.equals(robj2.uniqueValue())) {
                                String hasKey = connection.hget(table, uniqueValue);
                                if (hasKey != null) {
                                    throw new RedisException("Unique key '" + uniqueValue + "' has exits");
                                }

                                Field keyField = DBObjectManager.getInsertIncrKeyField(table);
                                connection.hdel(table, robj2.uniqueValue());
                                String keyValue = keyField.get(obj).toString();
                                connection.hset(table, uniqueValue, keyValue);
                            }
                        }

//

                        connection.set(obj.toKeyString(table), obj.toDBJson(table).toString());
                    } catch (Exception e) {
                        throw new RedisException(e.getMessage());
                    }
                    return null;
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(final DBObject obj, final String table) throws DBException {
        try {
            new JedisExecute<Void>(pool) {
                @Override
                public Void execute(Jedis connection) throws RedisException {
                    try {
                        connection.del(obj.toKeyString(table));

                        if (obj instanceof RedisObject) {
                            RedisObject robj = (RedisObject) obj;
                            String uniqueValue = robj.uniqueValue();
                            if (uniqueValue != null) {
                                //                                transaction.hset(table, uniqueValue, incrId.toString());
                                connection.hdel(table, uniqueValue);
                            }
                        }
                    } catch (DBException e) {
                        throw new RedisException(e.reason());
                    }
                    return null;
                }
            }.run();
        } catch (RedisException e) {
//            e.printStackTrace();
            throw new DBException(e.reason());
        }
    }

    @Override
    public void insert(final DBObject obj, final String table) throws DBException {
        try {
            new JedisExecute<Void>(pool) {
                @Override
                public Void execute(Jedis connection) throws RedisException {
                    if (obj instanceof RedisObject) {
                        RedisObject robj = (RedisObject) obj;
                        String uniqueValue = robj.uniqueValue();
                        if (uniqueValue != null) {
                            //                                transaction.hset(table, uniqueValue, incrId.toString());
                            String hasKey = connection.hget(table, uniqueValue);
                            if (hasKey != null) {
                                throw new RedisExistException("Unique key '" + uniqueValue + "' has exist");
                            }
                        }
                    }

                    try {

                        Field keyField = DBObjectManager.getInsertIncrKeyField(table);
                        //                    Transaction transaction = connection.multi();
                        if (keyField != null) {
                            Long incrId = connection.incr("next_" + table + "_id");
                            if (keyField.getType().equals(Integer.TYPE)) {
                                keyField.set(obj, incrId.intValue());
                            } else if (keyField.getType().equals(Long.TYPE)) {
                                keyField.set(obj, incrId);
                            } else if (keyField.getType().equals(String.class)) {
                                keyField.set(obj, incrId.toString());
                            }

                            if (obj instanceof RedisObject) {
                                RedisObject robj = (RedisObject) obj;
                                String uniqueValue = robj.uniqueValue();
                                if (uniqueValue != null) {
                                    //                                transaction.hset(table, uniqueValue, incrId.toString());
                                    connection.hset(table, uniqueValue, incrId.toString());
                                }
                            }
                        } else {
                            String value = connection.get(obj.toKeyString(table));
                            if (value != null) {
                                throw new RedisExistException("Has exist data.");
                            }
                        }
                        //                    transaction.set(obj.toKeyString(table), obj.toDBJson(table).toString());
                        //                    transaction.exec();
//                        System.out.println("table key:"+obj.getKeyStringByRegisterKey(table));
                        connection.set(obj.getKeyStringByRegisterKey(table), obj.toDBJson(table).toString());
                    } catch (Exception e) {
//                        e.printStackTrace();
                        throw new RedisException(e.getMessage());
                    }
                    return null;
                }
            }.run();
        } catch (RedisException e) {
//            e.printStackTrace();
            throw new DBException(e.reason());
        }
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

    public <T> T query(final JedisHandler<T> jsh) throws DBException {
        try {
            return new JedisExecute<T>(pool) {

                @Override
                public T execute(Jedis connection) throws RedisException {
                    return jsh.handle(connection);
                }
            }.run();
        } catch (RedisException e) {
//            e.printStackTrace();
            throw new DBException(e.reason());
        }
    }
}
