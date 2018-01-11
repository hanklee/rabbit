/**
 * Create time: 12-Jan-2018
 */
package com.lixianling.rabbit.mongo;

import com.lixianling.rabbit.dao.mongodb.MongoDAO;
import com.lixianling.rabbit.manager.MongoManager;
import com.lixianling.rabbit.redis.Gencontent;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;

/**
 *
 * @author hank
 */
public class MongoTest {
    public static void main(String[] args) throws Exception {
        MongoDAO dao = new MongoDAO();
        Gencontent tmp = new Gencontent();
        tmp.title = "testfafdasfdasf";
        tmp.id = 1;
        tmp.contents = new ArrayList<String>();
        tmp.contents.add("test1");
        tmp.contents.add("test2");
        System.out.println(tmp.toDBJson().toString());

        dao.insert(tmp);
    }
}
