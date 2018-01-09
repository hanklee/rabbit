/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-08 13:37
 */
package com.lixianling.rabbit;

import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.redis.JedisHandler;
import com.lixianling.rabbit.dao.redis.RedisDAO;
import com.lixianling.rabbit.dao.redis.RedisException;
import com.lixianling.rabbit.dao.redis.RedisObject;
import com.lixianling.rabbit.manager.DBObjectManager;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Xianling Li(hanklee)
 * $Id: RedisDBObject.java 39 2016-01-08 12:04:37Z hank $
 */
public class RedisDBObject extends RedisObject {
    private static final Object LOCK_INSERT = new Object();
    /*

    clear delete

    del myobjects:unique:email
    del myobjects:unique:name
    del myobjects:ids:
    del myobjects:next_id:
    del myobjects:xxx

     */

    public static final int MYCODE_EXIST_EMAIL = 100;
    public static final int MYCODE_EXIST_NAME = 101;

    public int id;
    public String name;
    public String email;

    @Override
    public void beforeInsert(Object obj) throws DBException {
        synchronized (LOCK_INSERT) {
            if (obj instanceof Jedis) {
                Jedis con = (Jedis) obj;
                String uniqueEmail = this.getTableName() + RedisDAO.TABLE_UNIQUE + "email";
                String uniqueName = this.getTableName() + RedisDAO.TABLE_UNIQUE + "name";
                String temp = con.hget(uniqueEmail, this.email);
                if (temp != null) {
                    throw new DBException("has exist email", MYCODE_EXIST_EMAIL);
                }
                temp = con.hget(uniqueName, this.name);
                if (temp != null) {
                    throw new DBException("has exist name", MYCODE_EXIST_NAME);
                }
                con.hset(uniqueEmail, this.email, this.getKeyStringByRegisterKey(this.getTableName()));
                con.hset(uniqueName, this.name, this.getKeyStringByRegisterKey(this.getTableName()));
            }
        }
    }

    @Override
    public void beforeUpdate(Object obj) throws DBException {
        synchronized (LOCK_INSERT) {
            if (obj instanceof Jedis) {
                Jedis con = (Jedis) obj;
                RedisDBObject clone = (RedisDBObject) this.clone();
                String value = con.get(this.toKeyString(this.getTableName()));
                clone.JsonToObj(new JSONObject(value));
                if (!this.email.equals(clone.email)) {
                    String uniqueEmail = this.getTableName() + RedisDAO.TABLE_UNIQUE + "email";
                    String temp = con.hget(uniqueEmail, this.email);
                    if (temp != null) {
                        throw new DBException("has exist email", MYCODE_EXIST_EMAIL);
                    }
                    con.hdel(uniqueEmail, clone.email);
                    con.hset(uniqueEmail, this.email, this.getKeyStringByRegisterKey(this.getTableName()));
                }
                if (!this.name.equals(clone.name)) {
                    String uniqueName = this.getTableName() + RedisDAO.TABLE_UNIQUE + "name";
                    String temp = con.hget(uniqueName, this.name);
                    if (temp != null) {
                        throw new DBException("has exist name", MYCODE_EXIST_NAME);
                    }
                    con.hdel(uniqueName, clone.name);
                    con.hset(uniqueName, this.name, this.getKeyStringByRegisterKey(this.getTableName()));
                }
            }
        }
    }

    @Override
    public void afterDelete(Object obj) throws DBException {
        synchronized (LOCK_INSERT) {
            if (obj instanceof Jedis) {
                // After must using Pipeline for this exception:
                // Cannot use Jedis when in Pipeline. Please use Pipeline or reset jedis state .
                Pipeline con = ((Jedis) obj).pipelined();
                con.multi();
                String uniqueEmail = this.getTableName() + RedisDAO.TABLE_UNIQUE + "email";
                String uniqueName = this.getTableName() + RedisDAO.TABLE_UNIQUE + "name";
                con.hdel(uniqueName, this.name);
                con.hdel(uniqueEmail, this.email);
                con.exec();
            }
        }
    }


    private static void testInsert(DBObject obj, DAO dao) throws DBException {
        try {
            dao.insert(obj);
            System.out.println("insert success:" + obj.toJson().toString());
        } catch (DBException e) {
            switch (e.code()) {
                case MYCODE_EXIST_NAME:
                    System.out.println("has exist name");
                    break;
                case MYCODE_EXIST_EMAIL:
                    System.out.println("has exist email");
                    break;
                default:
                    System.out.println("unknown error");
            }
//            e.printStackTrace();
        }
    }

    private static void testUpdate(DBObject obj, DAO dao) {
        try {
            dao.update(obj);
            System.out.println("update success:" + obj.toJson().toString());
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    private static void testDelete(RedisDAO dao, String name) {
        try {
            RedisDBObject obj = testQuery(dao,name);
            if (obj != null) {
                dao.delete(obj);
                System.out.println("delete success:" + obj.toJson().toString());
            }

        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    private static RedisDBObject testQuery(RedisDAO dao, final String query) throws DBException {
        try {
            RedisDBObject obj1 = dao.query(new JedisHandler<RedisDBObject>() {
                @Override
                public RedisDBObject handle(Jedis connection) throws RedisException {
                    RedisDBObject obj = new RedisDBObject();
                    String uniqueName = obj.getTableName() + RedisDAO.TABLE_UNIQUE + "email";
                    String uniqueValue = connection.hget(uniqueName, query);
                    if (uniqueValue == null)
                        throw new RedisException("not found key");
                    String value = connection.get(uniqueValue);
                    if (value == null)
                        throw new RedisException("not found value");
                    obj.JsonToObj(new JSONObject(value));
                    return obj;
                }
            });
            System.out.println("query get obj:" + obj1.toJson().toString());
            return obj1;
        } catch (Exception e){
//            e.printStackTrace();
//            e.printStackTrace();
        }
        return null;
    }

    public static void testList(RedisDAO dao) {
        try {
            System.out.println("OBJECT LIST:");
            List<RedisDBObject> list = dao.query(new JedisHandler<List<RedisDBObject>>() {
                @Override
                public List<RedisDBObject> handle(Jedis connection) throws RedisException {
                    List<RedisDBObject> result = new ArrayList<RedisDBObject>();
                    Set<String> srs = connection.zrange(RedisDAO.getTableIds("myobjects"), 0, 5);
                    for (String sr : srs) {
                        RedisDBObject no = new RedisDBObject();
                        String value = connection.get(sr);
                        if (value != null) {
                            no.JsonToObj(new JSONObject(value));
                            result.add(no);
                        }
                    }
                    return result;
                }
            });
            for (RedisObject ro : list) {
                System.out.println(ro.toJson().toString());
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        RedisDBObject obj = new RedisDBObject();
        obj.id = 1;
        obj.name = "hank3";
        obj.email = "hank3.dev@gmail.com";
        System.out.println(obj.toJson().toString());
        System.out.println(obj.toKeyString(obj.getTableName()));
        System.out.println(obj.getKeyStringByRegisterKey(obj.getTableName()));
//        System.out.println(obj.uniqueValue());

        RedisDAO dao = new RedisDAO();

//        testDelete(dao,"hank2.dev@gmail.com");


        testInsert(obj,dao);

//        obj = testQuery(dao,"hank1.dev@gmail.com");
//        obj.email = "hank.dev@gmail.com";
//        testUpdate(obj,dao);

        testList(dao);

//        RedisDBObject obj2 = new RedisDBObject();
//        obj2.id = 6;
//
//        System.out.println(dao.getObject(obj2));

//        dao.update(obj1);

//        System.out.println(dao.get(obj2));


//        dao.delete(dao.getObject(obj2));
    }
}
