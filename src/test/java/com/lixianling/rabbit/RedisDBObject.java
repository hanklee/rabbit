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
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Xianling Li(hanklee)
 * $Id: RedisDBObject.java 39 2016-01-08 12:04:37Z hank $
 */
public class RedisDBObject extends RedisObject {

    public static final int MYCODE_EXIST_EMAIL = 100;
    public static final int MYCODE_EXIST_NAME = 101;

    public int id;
    public String name;
    public String email;

    @Override
    public void beforeInsert(Object con) throws DBException {
        if (con instanceof Jedis) {
            Jedis pipeline = (Jedis) con;
            String temp = pipeline.hget(this.getTableName() + "_email", this.email);
            if (temp != null) {
                throw new DBException("has exist email", MYCODE_EXIST_EMAIL);
            }
            pipeline.hset(this.getTableName() + "_email", this.email, this.getKeyStringByRegisterKey(this.getTableName()));
        }

    }


    private static void testInsert(DBObject obj, DAO dao) throws DBException {
        try {
            dao.insert(obj);
            System.out.println("insert success:" + obj.toJson().toString());
        } catch (DBException e) {
            switch (e.code()) {
                case 1:
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

    private static void testQuery(RedisDAO dao, final String query) throws DBException {
        RedisDBObject obj1 = dao.query(new JedisHandler<RedisDBObject>() {
            @Override
            public RedisDBObject handle(Jedis connection) throws RedisException {
                RedisDBObject obj = new RedisDBObject();
                String uniqueValue = connection.hget(obj.getTableName(), query);
//                System.out.println();
                if (uniqueValue == null)
                    throw new RedisException("not found");
                String key = obj.getTableName() + ":" + uniqueValue;
//                System.out.println(key);
                String value = connection.get(key);
                if (value == null)
                    throw new RedisException("not found");
                obj.JsonToObj(new JSONObject(value));
                return obj;
            }
        });

        System.out.println(obj1.toJson().toString());
    }

    public static void testList(RedisDAO dao) {
        try {
            List<RedisDBObject> list = dao.query(new JedisHandler<List<RedisDBObject>>() {
                @Override
                public List<RedisDBObject> handle(Jedis connection) throws RedisException {
                    List<RedisDBObject> result = new ArrayList<RedisDBObject>();
                    Set<String> srs = connection.zrange("myobjects" + RedisDAO.TABLE_IDS, 2, 4);
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
            for(RedisObject ro:list) {
                System.out.println(ro.toJson().toString());
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        RedisDBObject obj = new RedisDBObject();
        obj.id = 1;
        obj.name = "hank6";
        obj.email = "hank1.dev@gmail.com";
        System.out.println(obj.toJson().toString());
        System.out.println(obj.toKeyString(obj.getTableName()));
        System.out.println(obj.uniqueValue());

        RedisDAO dao = new RedisDAO();

        testList(dao);

//        testInsert(obj,dao);

//        testQuery(dao,"hank");

//        testUpdate(obj,dao);

//        RedisDBObject obj2 = new RedisDBObject();
//        obj2.id = 6;
//
//        System.out.println(dao.getObject(obj2));

//        dao.update(obj1);

//        System.out.println(dao.get(obj2));


//        dao.delete(dao.getObject(obj2));
    }
}
