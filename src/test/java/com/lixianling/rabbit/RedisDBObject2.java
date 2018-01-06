/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-08 20:54
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
 *         $Id: RedisDBObject2.java 40 2016-01-08 17:11:07Z hank $
 */
public class RedisDBObject2 extends RedisObject {

    public int id;
    public String name;
    public String email;


    public static void main(String[] args) throws Exception {

        RedisDBObject test = new RedisDBObject();
        System.out.println(test.getTableName());

        RedisDBObject2 obj = new RedisDBObject2();
        obj.id = 1;
        obj.name = "hank";
        obj.email = "hank.dev@gmail.com";
        System.out.println(obj.toJson().toString());
        System.out.println(obj.toKeyString(obj.getTableName()));
        System.out.println(obj.uniqueValue());

        RedisDAO dao = new RedisDAO();
        dao.insert(obj);

        obj.name = "hank2";

        dao.update(obj);

        final int id = obj.id;
        RedisDBObject2 obj1 = dao.query(new JedisHandler<RedisDBObject2>() {
            @Override
            public RedisDBObject2 handle(Jedis connection) throws RedisException {
                RedisDBObject2 obj = new RedisDBObject2();
                String key = obj.getTableName() + ":" + id;
                String value = connection.get(key);
                if (value == null)
                    throw new RedisException("not found");
                obj.JsonToObj(new JSONObject(value));
                return obj;
            }
        });

        System.out.println(obj1.toJson().toString());


        dao.delete(obj);
//        obj1.name = "hank2";
    }

}
