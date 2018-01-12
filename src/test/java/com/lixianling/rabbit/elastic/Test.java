/**
 * Create time: 11-Jan-2018
 */
package com.lixianling.rabbit.elastic;

import com.lixianling.rabbit.dao.elastic.ElasticDAO;
import com.lixianling.rabbit.redis.Gencontent;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author hank
 */
public class Test {

    public static void testPerformance(int testNum) throws Exception {
        ElasticDAO dao = new ElasticDAO();


//        System.out.println(tmp.toDBJson().toString());
        List<Gencontent> list = new ArrayList<Gencontent>();
        System.out.println("Insert performance ... , number: " + testNum);
        long cTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            Gencontent tmp = new Gencontent();
            tmp.title = "testfafdasfdasf";
            tmp.id = i + 1;
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
            tmp.id = 0;
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

        testPerformance(100);
//        dao.insert(tmp);
//        System.out.println("insert data _id:"+tmp._id);
//        tmp._id = "AWDlwO5wsNOZMvBpbFnI";
//        XContentBuilder json = jsonBuilder()
//                .startObject();
//        Map<String,Object> data = tmp.ObjToMap(tmp.getTableName());
//        for (String key:data.keySet()) {
//            json.field(key,data.get(key));
//        }
//        json.endObject();
//        System.out.println(json.string());
//        XContentBuilder builder = jsonBuilder()
//                .startObject()
//                .field("user", "kimchy")
//                .field("postDate", new Date())
//                .field("message", "trying out Elasticsearch")
//                .endObject();
//        System.out.println(builder.string());
//        dao.update(tmp);
//        Gencontent no = (Gencontent) dao.getObject(tmp);
//        System.out.println("get data:"+no.toDBJson().toString());

//        dao.delete(tmp);
//        System.out.println("delete...");

        //AWDliGCwsNOZMvBpbFnD
        //AWDlqSDDsNOZMvBpbFnF
    }
}
