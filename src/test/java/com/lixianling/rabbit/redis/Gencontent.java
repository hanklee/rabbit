/**
 * Create time: 09-Jan-2018
 */
package com.lixianling.rabbit.redis;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.RedisDBObject;
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
 * @author hank
 */
public class Gencontent extends DBObject {

    public int id;
    public String title;
    //    public String parsecon;
    public List<String> contents;

    public static void testList(RedisDAO dao) {
        try {
            System.out.println("OBJECT LIST:");
            List<Gencontent> list = dao.query(new JedisHandler<List<Gencontent>>() {
                @Override
                public List<Gencontent> handle(Jedis connection) throws RedisException {
                    List<Gencontent> result = new ArrayList<Gencontent>();
                    Set<String> srs = connection.zrange(RedisDAO.getTableIds("gencontent"), 0, 20);
                    for (String sr : srs) {
                        Gencontent no = new Gencontent();
                        String value = connection.get(sr);
                        if (value != null) {
//                            System.out.println(value);
                            no.JsonToObj(new JSONObject(value));
                            result.add(no);
                        }
                    }
                    return result;
                }
            });
            for (Gencontent ro : list) {
                System.out.println(ro.id + "," + ro.title);
//                System.out.println(ro.contents);
                for (String content : ro.contents) {
                    System.out.println(content);
                }
                System.out.println("---------------------------");
//                System.out.println(ro.toJson().toString());
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    public static void cleanTable(RedisDAO dao, final String table) {
        try {
            System.out.println("Clean table: " + table);
            dao.query(new JedisHandler<Void>() {
                @Override
                public Void handle(Jedis connection) throws RedisException {
                    long totalNum = connection.zcount(RedisDAO.getTableIds(table), "-inf", "+inf");
                    long delNum = totalNum / 20;
                    if (totalNum % 20 != 0) delNum++;
                    for (int i = 0; i < delNum; i++) {
                        Set<String> srs = connection.zrange(RedisDAO.getTableIds(table), 0, 19);
                        String[] dels = srs.toArray(new String[srs.size()]);
                        connection.del(dels);
                        connection.zrem(RedisDAO.getTableIds(table), dels);
                    }
                    return null;
                }
            });
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Gencontent tmp = new Gencontent();
        tmp.title = "test";
        tmp.id = 1;
        tmp.contents = new ArrayList<String>();
        tmp.contents.add("test1");
        tmp.contents.add("test2");
        System.out.println(tmp.toDBJson().toString());

        Gencontent tmp2 = new Gencontent();
        tmp2.JsonToObj(new JSONObject("{\"contents\":[\"test1\",\"test2\"],\"id\":1,\"title\":\"test\"}"));
        System.out.println(tmp2.contents);
        RedisDAO dao = new RedisDAO();
        testList(dao);
//        cleanTable(dao,"gencontent");
    }

    /*

    html txt has <br/>

    pure text length has 30


     */
}
