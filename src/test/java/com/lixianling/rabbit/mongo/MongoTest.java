/**
 * Create time: 12-Jan-2018
 */
package com.lixianling.rabbit.mongo;

import com.lixianling.rabbit.dao.mongodb.MongoDAO;
import com.lixianling.rabbit.dao.redis.RedisDAO;
import com.lixianling.rabbit.manager.MongoManager;
import com.lixianling.rabbit.redis.Gencontent;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hank
 */
public class MongoTest {

    public static void testPerformance(int testNum) throws Exception {
        MongoDAO dao = new MongoDAO();


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

    public static void main(String[] args) throws Exception {
        testPerformance(10000);
//        MongoDAO dao = new MongoDAO();
//        Gencontent tmp = new Gencontent();
//        tmp.title = "testfafdasfdasf";
//        tmp.id = 1;
//        tmp.contents = new ArrayList<String>();
//        tmp.contents.add("test1");
//        tmp.contents.add("test2");
//        System.out.println(tmp.toDBJson().toString());
//
//        dao.insert(tmp);
//        System.out.println(tmp._id);
//        tmp.title = "aaaaaaaa";
//        dao.update(tmp);
//        System.out.println(tmp._id);
////        System.out.println(tmp.toDBJson().toString());
//        Gencontent no = (Gencontent) dao.getObject(tmp);
//        System.out.println(no.toDBJson().toString());
//
//        dao.delete(no);

//        long cTime = System.currentTimeMillis();
//        for (int i = 0; i < 100; i++) {
//            dao.insert(tmp);
//        }
//        System.out.println("insert 100 run:" +(System.currentTimeMillis() - cTime) + " ms");
    }
}
