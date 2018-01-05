/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 22:49
 */
package com.lixianling.rabbit.dao.redis;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.manager.DBObjectManager;
import com.lixianling.rabbit.cache.CacheItem;
import com.lixianling.rabbit.manager.CacheManager;
import com.lixianling.rabbit.dao.CacheDAO;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.manager.RedisManager;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: RedisCacheDAO.java 39 2016-01-08 12:04:37Z hank $
 */
public class RedisCacheDAO extends CacheDAO {
    private final JedisPool pool;

    public RedisCacheDAO() {
        pool = RedisManager.getPool();
    }

    public void update(DBObject obj) throws DBException {
        update(obj, obj.getTableName());
    }

    public void delete(DBObject obj) throws DBException {
        delete(obj, obj.getTableName());
    }

    public void insert(DBObject obj) throws DBException {
        insert(obj, obj.getTableName());
    }

    /**
     * 写redis数据
     *
     * @param key
     * @param value
     * @throws DBException
     */
    private void redisSetValue(final String key, final String value) throws DBException {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
            throw new DBException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }

    public void delete(DBObject obj, String table) throws DBException {
        redisSetValue(obj.deleteKEY(table), obj.toDBJson(table).toString());
    }

    public void update(DBObject obj, String table) throws DBException {
        redisSetValue(obj.updateKEY(table), obj.toDBJson(table).toString());
    }

    public void insert(DBObject obj, String table) throws DBException {
        redisSetValue(obj.insertKEY(table), obj.toDBJson(table).toString());
    }

    @Override
    public void update(Collection<? extends DBObject> objs, String table_name) throws DBException {
        //
        throw new DBException("no implements.");
    }

    @Override
    public void insert(Collection<? extends DBObject> objs, String table_name) throws DBException {
        throw new DBException("no implements.");
    }

    @Override
    public void delete(Collection<? extends DBObject> objs, String table_name) throws DBException {
        throw new DBException("no implements.");
    }


    public void cleanData(String table, String key) throws DBException {
        String uKeys = CacheManager.CACHE_SUFFIX + key + CacheManager.UPDATE_CACHE_OP + table + "*";
        String dKeys = CacheManager.CACHE_SUFFIX + key + CacheManager.DELETE_CACHE_OP + table + "*";
        String iKeys = CacheManager.CACHE_SUFFIX + key + CacheManager.INSERT_CACHE_OP + table + "*";

        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<String> result = jedis.keys(uKeys);
            if (result.size() > 0)
                jedis.del(result.toArray(new String[result.size()]));

            result = jedis.keys(dKeys);
            if (result.size() > 0)
                jedis.del(result.toArray(new String[result.size()]));

            result = jedis.keys(iKeys);
            if (result.size() > 0)
                jedis.del(result.toArray(new String[result.size()]));
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
            throw new DBException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }

    public void cleanData(String table) throws DBException {
        String uKeys = CacheManager.CACHE_SUFFIX + "*" + CacheManager.UPDATE_CACHE_OP + table + "*";
        String dKeys = CacheManager.CACHE_SUFFIX + "*" + CacheManager.DELETE_CACHE_OP + table + "*";
        String iKeys = CacheManager.CACHE_SUFFIX + "*" + CacheManager.INSERT_CACHE_OP + table + "*";

        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<String> result = jedis.keys(uKeys);
            if (result.size() > 0)
                jedis.del(result.toArray(new String[result.size()]));

            result = jedis.keys(dKeys);
            if (result.size() > 0)
                jedis.del(result.toArray(new String[result.size()]));

            result = jedis.keys(iKeys);
            if (result.size() > 0)
                jedis.del(result.toArray(new String[result.size()]));
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
            throw new DBException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public void cleanDataByKey(String key) throws DBException {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.del(key);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
            throw new DBException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }

    public void cleanDataToDB(String table, String myKey, DAO dao) throws DBException {
        String pattern = CacheManager.CACHE_SUFFIX + myKey + "_*_" + table + ":*";
        _cleanDataToDB(pattern, dao);
    }

    public void cleanDataToDB(String table, DAO dao) throws DBException {
        String pattern = CacheManager.CACHE_SUFFIX + "*_" + table + ":*";
        _cleanDataToDB(pattern, dao);
    }

    public void cleanDataToDBByKey(String key, DAO dao) throws DBException {
        String pattern = CacheManager.CACHE_SUFFIX + key + "_*";
        _cleanDataToDB(pattern, dao);
    }


    private void _cleanDataToDB(final String pattern, DAO dao) throws DBException {
        Set<String> keys;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            keys = jedis.keys(pattern);
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
            throw new DBException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }

        List<CacheItem> dels = new ArrayList<CacheItem>();
        List<CacheItem> inss = new ArrayList<CacheItem>();
        List<CacheItem> udps = new ArrayList<CacheItem>();
        if (keys.size() == 0) {
            return;
        }

        for (String key : keys) {
            String[] key_split = key.split("_");
            if (key_split.length > 3) {
                String itemKey = key_split[1];
                String op = "_" + key_split[2] + "_";
                String value = RedisManager.get(key);
                String table_name;
                if (key_split.length > 4) {
                    table_name = key_split[3];
                    for (int i = 4; i < key_split.length; i++) {
                        table_name = table_name + "_" + key_split[i];
                    }
                    table_name = table_name.split(":")[0];
                } else {
                    table_name = key_split[3].split(":")[0];
                }
//                System.err.println("key:"+key); //update hero
//                System.err.println("value:"+value); //update hero
                try {
                    DBObject obj = (DBObject) DBObjectManager.getClassByTable(table_name).newInstance();
                    obj.JsonToObj(new JSONObject(value));
                    CacheItem item = new CacheItem(table_name, obj);
                    if (op.equals(CacheManager.DELETE_CACHE_OP)) {
                        dels.add(item);
                    } else if (op.equals(CacheManager.UPDATE_CACHE_OP)) {
                        udps.add(item);
                    } else if (op.equals(CacheManager.INSERT_CACHE_OP)) {
                        inss.add(item);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (CacheItem item : inss) {
            try {
                dao.insert(item.obj, item.table_name);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        for (CacheItem item : udps) {
            try {
                dao.update(item.obj, item.table_name);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        for (CacheItem item : dels) {
            try {
                dao.delete(item.obj, item.table_name);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        try {
            jedis = pool.getResource();
            jedis.del(keys.toArray(new String[keys.size()]));
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
            throw new DBException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }
}
