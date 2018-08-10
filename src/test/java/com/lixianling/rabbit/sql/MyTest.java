/**
 * Create time: 06-Jan-2018
 */
package com.lixianling.rabbit.sql;

import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.sql.SQLDAO;
import com.mongodb.DB;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hank
 */
public class MyTest extends TestCase {


    public static class MyTestData extends DBObject {
        public String table_name = "myTest";

        public int a;
        public int b;
        public int c;
        public int d;
    }


    public void test100(){
        try {
            test_Performance(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test1000(){
        try {
            test_Performance(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void test_Performance(int testNum) throws Exception {
        DAO dao = new SQLDAO();


//        System.out.println(tmp.toDBJson().toString());
        List<MyTestData> list = new ArrayList<MyTestData>();
        System.out.println("Insert performance ... , number: " + testNum);
        long cTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            MyTestData test = new MyTestData();

            test.a = 1;
            test.b = 3;
            test.c = 4;
            test.d = 5;
            test.a = i + 203;
            dao.insert(test);
            list.add(test);
        }
        System.out.println("run:" + (System.currentTimeMillis() - cTime) + " ms");
        System.out.println("Get    performance ... , number: " + testNum);
        cTime = System.currentTimeMillis();
        for (MyTestData tmp : list) {
            dao.getObject("myTest", tmp.a, tmp.b);
        }
        System.out.println("run:" + (System.currentTimeMillis() - cTime) + " ms");
        System.out.println("Update performance ... , number: " + testNum);
        cTime = System.currentTimeMillis();
        for (MyTestData tmp : list) {
//            tmp.id = 0;
            tmp.d = 3;
            dao.update(tmp);
        }
        System.out.println("run:" + (System.currentTimeMillis() - cTime) + " ms");
        System.out.println("Delete performance ... , number: " + testNum);
        cTime = System.currentTimeMillis();
        for (MyTestData tmp : list) {
            dao.delete(tmp);
        }
        System.out.println("run:" + (System.currentTimeMillis() - cTime) + " ms");
    }

//    public static void main(String[] args) throws Exception {
//        MyTest myTest = new MyTest();
//        myTest.testPerformance(100);
////        DAO dao = new SQLDAO();
////        Set<String> primary_keys = DBObjectManager.getTablePrimaryKey("myTest");
//
////        System.out.println(primary_keys);
//
////        System.out.println("insert 100 run:" +(System.currentTimeMillis() - cTime) + " ms");
//
//    }
}
