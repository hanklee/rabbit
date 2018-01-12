/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-08 20:54
 */
package com.lixianling.rabbit;

import com.lixianling.rabbit.dao.DAOHandler;
import com.lixianling.rabbit.dao.redis.RedisDAO;
import com.lixianling.rabbit.dao.redis.RedisObject;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Xianling Li(hanklee)
 *         $Id: RedisDBObject2.java 40 2016-01-08 17:11:07Z hank $
 */
public class RedisDBObject2 extends RedisObject {

    public int id;
    public String name;
    public String email;

    public static void testList(RedisDAO dao) {
        try {
            System.out.println("OBJECT LIST:");
            List<RedisDBObject2> list = dao.execute(new DAOHandler<List<RedisDBObject2>>() {
                @Override
                public List<RedisDBObject2> handle(Object con) throws DBException {
                    Jedis connection = (Jedis) con;
                    List<RedisDBObject2> result = new ArrayList<RedisDBObject2>();
                    Set<String> srs = connection.zrange(RedisDAO.getTableIds("myobjects2"), 0, 5);
                    for (String sr : srs) {
                        RedisDBObject2 no = new RedisDBObject2();
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

        RedisDBObject test = new RedisDBObject();
        System.out.println(test.getTableName());

        RedisDBObject2 obj = new RedisDBObject2();
        obj.id = 1;
        obj.name = "hank";
        obj.email = "hank.dev@gmail.com";
        System.out.println(obj.toJson().toString());
        System.out.println(obj.toKeyString(obj.getTableName()));
//        System.out.println(obj.uniqueValue());

        RedisDAO dao = new RedisDAO();
//        dao.insert(obj);

        obj.name = "hank2";
//        dao.update(obj);

//        final int id = obj.id;
//        RedisDBObject2 obj1 = dao.execute(new JedisHandler<RedisDBObject2>() {
//            @Override
//            public RedisDBObject2 handle(Jedis connection) throws RedisException {
//                RedisDBObject2 obj = new RedisDBObject2();
////                String key = "myobjects2" + RedisDAO.TABLE_IDS;
//                String value = connection.get("myobjects2:" + Integer.toString(id));
//                if (value == null)
//                    throw new RedisException("not found");
//                obj.JsonToObj(new JSONObject(value));
//                return obj;
//            }
//        });
//
//        System.out.println(obj1.toJson().toString());

        testList(dao);

//        dao.delete(obj);
//        obj1.name = "hank2";
    }

}
