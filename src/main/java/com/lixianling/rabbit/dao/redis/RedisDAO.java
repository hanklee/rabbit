/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-07 00:45
 */
package com.lixianling.rabbit.dao.redis;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.conf.RabbitConfig;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.manager.DBObjectManager;
import com.lixianling.rabbit.manager.RabbitManager;
import com.lixianling.rabbit.manager.RedisManager;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.lang.reflect.Field;
import java.util.Collection;

import static com.lixianling.rabbit.DBException.CODE_EXIST_VALUE;
import static com.lixianling.rabbit.DBException.CODE_NOTFOUND;

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

    public final static String TABLE_IDS = "_ids";
    public final static String TABLE_NEXT_ID = "_next_id";

    public RedisDAO() {
        if (RabbitManager.RABBIT_CONFIG.mode == RabbitConfig.Mode.MIX
                || RabbitManager.RABBIT_CONFIG.mode == RabbitConfig.Mode.REDIS) {
            this.pool = RedisManager.getPool();
        } else {
            throw new RuntimeException("must config the redis in rabbit.xml file.");
        }
    }

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
                            throw new RedisException("Not found data.",CODE_NOTFOUND);
                        }
                        obj.beforeUpdate(connection);
                        Pipeline pipeline = connection.pipelined();
                        pipeline.multi();
                        DBObject clone = obj.clone();
                        clone.JsonToObj(new JSONObject(value));
                        String uniqueValue = obj.uniqueValue();
                        if (uniqueValue != null
                                && !uniqueValue.equals(clone.uniqueValue())) {
                            String hasKey = connection.hget(table, uniqueValue);
                            if (hasKey != null) {
                                throw new RedisException("Unique key '" + uniqueValue + "' has exits", CODE_EXIST_VALUE);
                            }

                            Field keyField = DBObjectManager.getInsertIncrKeyField(table);
                            if (keyField != null) {
                                pipeline.hdel(table, clone.uniqueValue());
                                String keyValue = keyField.get(obj).toString();
                                pipeline.hset(table, uniqueValue, keyValue);
                            }
                        }
                        pipeline.set(obj.toKeyString(table), obj.toDBJson(table).toString());
                        pipeline.exec();
                    } catch (Exception e) {
                        throw new RedisException(e.getMessage());
                    }
                    return null;
                }
            }.run();
        } catch (RedisException e) {
            throw e;
        }catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void delete(final DBObject obj, final String table) throws DBException {
        try {
            new JedisExecute<Void>(pool) {
                @Override
                public Void execute(Jedis connection) throws RedisException {
                    try {
                        obj.beforeDelete(connection);
                        Pipeline pipeline = connection.pipelined();
                        pipeline.multi();
                        pipeline.del(obj.toKeyString(table));
                        String uniqueValue = obj.uniqueValue();
                        if (uniqueValue != null) {
                            //                                transaction.hset(table, uniqueValue, incrId.toString());

                            pipeline.hdel(table, uniqueValue);
                        }
//                        pipeline.srem(table + TABLE_IDS, obj.toKeyString(table));
                        pipeline.zrem(table + TABLE_IDS, obj.toKeyString(table));
                        pipeline.exec();
                    } catch (Exception e) {
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
    public void insert(final DBObject obj, final String table) throws DBException {
        try {
            new JedisExecute<Void>(pool) {
                @Override
                public Void execute(Jedis connection) throws RedisException {

                    String uniqueValue = obj.uniqueValue();
                    if (uniqueValue != null) {
                        //                                transaction.hset(table, uniqueValue, incrId.toString());
                        String hasKey = connection.hget(table, uniqueValue);
                        if (hasKey != null) {
                            throw new RedisException("Unique key '" + uniqueValue + "' has exist", CODE_EXIST_VALUE);
                        }
                    }
                    Pipeline pipeline = connection.pipelined();
                    try {
                        obj.beforeInsert(connection);
                        Long incrId = connection.incr(table + TABLE_NEXT_ID);
                        Field keyField = DBObjectManager.getInsertIncrKeyField(table);
                        //                    Transaction transaction = connection.multi();
                        if (keyField != null) {
                            if (keyField.getType().equals(Integer.TYPE)) {
                                keyField.set(obj, incrId.intValue());
                            } else if (keyField.getType().equals(Long.TYPE)) {
                                keyField.set(obj, incrId);
                            } else if (keyField.getType().equals(String.class)) {
                                keyField.set(obj, incrId.toString());
                            }

                            pipeline.multi();
//                            pipeline.sadd(table + TABLE_IDS, obj.getKeyStringByRegisterKey(table));
                            uniqueValue = obj.uniqueValue();
                            if (uniqueValue != null) {
                                //                                transaction.hset(table, uniqueValue, incrId.toString());
                                pipeline.hset(table, uniqueValue, incrId.toString());
                            }
                        } else {
                            String value = connection.get(obj.toKeyString(table));
                            if (value != null) {
                                throw new RedisException("Has exist data.", CODE_EXIST_VALUE);
                            }
                            pipeline.multi();
                        }
                        pipeline.zadd(table + TABLE_IDS, incrId, obj.getKeyStringByRegisterKey(table));
                        pipeline.set(obj.getKeyStringByRegisterKey(table), obj.toDBJson(table).toString());
                        pipeline.exec();
                    } catch (RedisException e) {
                        throw e;
                    } catch (DBException e) {
                        throw new RedisException(e.reason(),e.code());
                    }catch (Exception e) {
                        throw new RedisException(e.getMessage());
                    }
                    return null;
                }
            }.run();
        } catch (RedisException e) {
//            e.printStackTrace();
            throw new DBException(e.reason(),e.code());
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

    @Override
    public DBObject getObject(final DBObject obj, final String table) throws DBException {
        try {
            return new JedisExecute<DBObject>(pool) {
                @Override
                public DBObject execute(Jedis connection) throws RedisException {
                    try {
                        String value = connection.get(obj.toKeyString(table));
                        if (value == null)
                            throw new RedisException("not found!");
                        DBObject clone = obj.clone();
                        clone.JsonToObj(new JSONObject(value));
                        return clone;
                    } catch (DBException e) {
                        throw new RedisException(e.reason());
                    }
                }
            }.run();
        } catch (RedisException e) {
            throw new DBException(e.reason());
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
