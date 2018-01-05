/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-08 13:37
 */
package com.lixianling.rabbit;

import com.lixianling.rabbit.dao.redis.JedisHandler;
import com.lixianling.rabbit.dao.redis.RedisDAO;
import com.lixianling.rabbit.dao.redis.RedisException;
import com.lixianling.rabbit.dao.redis.RedisObject;
import com.lixianling.rabbit.manager.RedisManager;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

/**
 * @author Xianling Li(hanklee)
 *         $Id: MyDBObject.java 39 2016-01-08 12:04:37Z hank $
 */
public class MyDBObject extends RedisObject {

    public int id;
    public String name;
    public String email;


    public static void main(String[] args) throws Exception {
        MyDBObject obj = new MyDBObject();
        obj.id = 1;
        obj.name = "hank";
        obj.email = "hank.dev@gmail.com";
        System.out.println(obj.toJson().toString());
        System.out.println(obj.toKeyString(obj.getTableName()));
        System.out.println(obj.uniqueValue());

        RedisDAO dao = new RedisDAO(RedisManager.getPool());
//        dao.delete(obj);
//        dao.insert(obj);
        final String queryName = "hank";
        MyDBObject obj1 = dao.query(new JedisHandler<MyDBObject>() {
            @Override
            public MyDBObject handle(Jedis connection) throws RedisException {
                MyDBObject obj = new MyDBObject();
                String uniqueValue = connection.hget(obj.getTableName(), queryName);
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

        obj1.name = "hank2";

        MyDBObject obj2 = new MyDBObject();
        obj2.id = 4;

        System.out.println(dao.get(obj2));

//        dao.update(obj1);

//        System.out.println(dao.get(obj2));


//        dao.delete(dao.get(obj2));
    }
}
