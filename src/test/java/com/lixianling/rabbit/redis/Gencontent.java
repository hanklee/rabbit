/**
 * Create time: 09-Jan-2018
 */
package com.lixianling.rabbit.redis;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAOHandler;
import com.lixianling.rabbit.dao.redis.RedisDAO;
import com.lixianling.rabbit.dao.redis.RedisException;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author hank
 */
public class Gencontent extends DBObject {
    public String _id;
    public int id;
    public String title;
    //    public String parsecon;
    public List<String> contents;

    public static void testList(RedisDAO dao) {
        try {
            System.out.println("OBJECT LIST:");
            List<Gencontent> list = dao.execute(new DAOHandler<List<Gencontent>>() {
                @Override
                public List<Gencontent> handle(Object con) throws RedisException {
                    Jedis connection = (Jedis) con;
                    int si = 0;
                    int start = si * 20 + 1;
                    if (start == 1) start = 0;
                    int end = start + 20;
                    List<Gencontent> result = new ArrayList<Gencontent>();
                    Set<String> srs = connection.zrange(RedisDAO.getTableIds("gencontent"), start, end);
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
            dao.execute(new DAOHandler<Void>() {
                @Override
                public Void handle(Object con) throws DBException {
                    Jedis connection = (Jedis) con;
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

    public static void testPerformance(int testNum) throws Exception {
        RedisDAO dao = new RedisDAO();


//        System.out.println(tmp.toDBJson().toString());
        List<Gencontent> list = new ArrayList<Gencontent>();
        System.out.println("Insert performance ... , number: " + testNum);
        long cTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            Gencontent tmp = new Gencontent();
            tmp.title = "testfafdasfdasf";
//            tmp.id = i + 1;
            tmp.contents = new ArrayList<String>();
            tmp.contents.add("test1");
            tmp.contents.add("test2");
            dao.insert(tmp);
            list.add(tmp);
        }
        System.out.println("run:" + (System.currentTimeMillis() - cTime) + " ms");
        System.out.println("Get    performance ... , number: " + testNum);
        cTime = System.currentTimeMillis();
        for (Gencontent tmp : list) {
            dao.getObject(tmp);
        }
        System.out.println("run:" + (System.currentTimeMillis() - cTime) + " ms");
        System.out.println("Update performance ... , number: " + testNum);
        cTime = System.currentTimeMillis();
        for (Gencontent tmp : list) {
//            tmp.id = 0;
            tmp.title = "afdsfssf";
            dao.update(tmp);
        }
        System.out.println("run:" + (System.currentTimeMillis() - cTime) + " ms");
        System.out.println("Delete performance ... , number: " + testNum);
        cTime = System.currentTimeMillis();
        for (Gencontent tmp : list) {
            dao.delete(tmp);
        }
        System.out.println("run:" + (System.currentTimeMillis() - cTime) + " ms");
    }

    public static void main(String[] args) throws Exception{
        testPerformance(10000);
//        Gencontent tmp = new Gencontent();
//        tmp.title = "test";
//        tmp.id = 1;
//        tmp.contents = new ArrayList<String>();
//        tmp.contents.add("test1");
//        tmp.contents.add("test2");
//        System.out.println(tmp.toDBJson().toString());
//
//        Gencontent tmp2 = new Gencontent();
//        tmp2.JsonToObj(new JSONObject("{\"contents\":[\"test1\",\"test2\"],\"id\":1,\"title\":\"test\"}"));
//        System.out.println(tmp2.contents);
//        RedisDAO dao = new RedisDAO();
////        testList(dao);
////        cleanTable(dao,"gencontent");
//        long cTime = System.currentTimeMillis();
//        for (int i = 0; i < 100; i++) {
//            dao.insert(tmp);
//        }
//        System.out.println("insert 100 run:" +(System.currentTimeMillis() - cTime) + " ms");
    }

    /*

    html txt has <br/>

    pure text length has 30


     */
}
