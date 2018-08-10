/**
 * Create time: 2018-08-10
 */
package com.lixianling.rabbit.table;

import com.alibaba.fastjson.JSONObject;
import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.JSONObj;
import com.lixianling.rabbit.RedisDBObject;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xianling Li
 */
public class JsonTableTest extends TestCase {

    public void test_v1() throws DBException {
        Myobject obj = new Myobject();
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
//        System.out.println(obj.toJson().toString());
//        System.out.println(obj.toDBJson().toString());
        Assert.assertEquals("myobjects2:1", obj.keyString());
    }

    public void test_v2() throws DBException {
        Myobject obj2 = JSONObj.newDataObj(Myobject.class, JSONObject.parseObject("{\"list1\":[1],\"list3\":[7.5,8.5,9.5],\"list2\":[],\"name\":\"hank3\",\"id\":1,\"email\":\"hank3.dev@gmail.com\"}"));
//        System.out.println(obj2.toDBJson().toString());
//        System.out.println(obj2.toJson());
        Assert.assertEquals("{\"list3\":[7.5,8.5,9.5],\"name\":\"hank3\",\"id\":1,\"email\":\"hank3.dev@gmail.com\"}", obj2.toDBJson().toString());
        Assert.assertEquals("myobjects2:1", obj2.keyString());
    }

    public static class Myobject extends DBObject {
        public int id;
        public String name;
        public String email;
        public List<Integer> list1;
        public List<String> list2;
        public List<Double> list3;
    }
}
