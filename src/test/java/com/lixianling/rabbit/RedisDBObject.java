/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-08 13:37
 */
package com.lixianling.rabbit;

import com.alibaba.fastjson.JSONObject;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.DAOHandler;
import com.lixianling.rabbit.dao.redis.RedisDAO;
//import org.json.JSONObject;
import junit.framework.TestCase;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Xianling Li(hanklee)
 * $Id: RedisDBObject.java 39 2016-01-08 12:04:37Z hank $
 */
public class RedisDBObject extends TestCase {

    public static class DataDBObject extends DBObject {
        public int id;
        public String name;
        public String email;
        public List<Integer> list1;
        public List<String> list2;
        public List<Double> list3;

            /*

    clear delete

    del myobjects:unique:email
    del myobjects:unique:name
    del myobjects:ids:
    del myobjects:next_id:
    del myobjects:xxx

     */

        @Override
        public void beforeInsert(DAO dao,String table, Object obj) throws DBException {
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
        public void beforeUpdate(DAO dao,String table, Object obj) throws DBException {
            synchronized (LOCK_INSERT) {
                if (obj instanceof Jedis) {
                    Jedis con = (Jedis) obj;
                    DataDBObject clone = (DataDBObject) this.clone();
                    String value = con.get(this.toKeyString(this.getTableName()));
                    clone.JsonToObj((JSONObject) JSONObject.parse(value));
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
        public void afterDelete(DAO dao,String table, Object obj) throws DBException {
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
    }

    private static final Object LOCK_INSERT = new Object();
    public static final int MYCODE_EXIST_EMAIL = 100;
    public static final int MYCODE_EXIST_NAME = 101;

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
            DataDBObject obj = testQuery(dao, name);
            if (obj != null) {
                dao.delete(obj);
                System.out.println("delete success:" + obj.toJson().toString());
            }

        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    private static DataDBObject testQuery(RedisDAO dao, final String query) throws DBException {
        try {
            DataDBObject obj1 = dao.execute(new DAOHandler<DataDBObject>() {
                @Override
                public DataDBObject handle(Object con) throws DBException {
                    Jedis connection = (Jedis) con;
                    DataDBObject obj = new DataDBObject();
                    String uniqueName = obj.getTableName() + RedisDAO.TABLE_UNIQUE + "email";
                    String uniqueValue = connection.hget(uniqueName, query);
                    if (uniqueValue == null)
                        throw new DBException("not found key");
                    String value = connection.get(uniqueValue);
                    if (value == null)
                        throw new DBException("not found value");
                    obj.JsonToObj((JSONObject) JSONObject.parse(value));
                    return obj;
                }
            });
            System.out.println("execute get obj:" + obj1.toJson().toString());
            return obj1;
        } catch (Exception e) {
//            e.printStackTrace();
//            e.printStackTrace();
        }
        return null;
    }

    public static void testList(RedisDAO dao) {
        try {
            System.out.println("OBJECT LIST:");
            List<DataDBObject> list = dao.execute(new DAOHandler<List<DataDBObject>>() {
                @Override
                public List<DataDBObject> handle(Object con) throws DBException {
                    Jedis connection = (Jedis) con;
                    List<DataDBObject> result = new ArrayList<DataDBObject>();
                    Set<String> srs = connection.zrange(RedisDAO.getTableIds("myobjects"), 0, 5);
                    for (String sr : srs) {
                        DataDBObject no = new DataDBObject();
                        String value = connection.get(sr);
                        if (value != null) {
                            no.JsonToObj((JSONObject) JSONObject.parse(value));
                            result.add(no);
                        }
                    }
                    return result;
                }
            });
            for (DBObject ro : list) {
                System.out.println(ro.toJson().toString());
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        DataDBObject obj = new DataDBObject();
        obj.id = 1;
        obj.name = "hank3";
        obj.email = "hank3.dev@gmail.com";
//        obj.list1 = new ArrayList<Integer>();
//        obj.list1.add(1);
//        obj.list2 = new ArrayList<String>();
        obj.list3 = new ArrayList<Double>();
        obj.list3.add(7.5);
        obj.list3.add(8.5);
        obj.list3.add(9.5);
        System.out.println(obj.toJson().toString());
        System.out.println(obj.toDBJson().toString());
        System.out.println(obj.toKeyString(obj.getTableName()));
        System.out.println(obj.getKeyStringByRegisterKey(obj.getTableName()));
//        System.out.println(obj.uniqueValue());

        DataDBObject obj2 = new DataDBObject();
        obj2.JsonToObj((JSONObject) JSONObject.parse("{\"list1\":[1],\"list3\":[7.5,8.5,9.5],\"list2\":[],\"name\":\"hank3\",\"id\":1,\"email\":\"hank3.dev@gmail.com\"}"));
        System.out.println(obj2.toJson().toString());
        System.out.println(obj2.toDBJson().toString());

        RedisDAO dao = new RedisDAO();

//        testDelete(dao,"hank2.dev@gmail.com");


//        testInsert(obj,dao);

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
