/**
 * Create time: 06-Jan-2018
 */
package com.lixianling.rabbit.sql;

import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.sql.SQLDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hank
 */
public class MyTest extends DBObject {
    public String table_name = "myTest";

    public int a;
    public int b;
    public int c;
    public int d;

    public static void testPerformance(int testNum) throws Exception {
        DAO dao = new SQLDAO();


//        System.out.println(tmp.toDBJson().toString());
        List<MyTest> list = new ArrayList<MyTest>();
        System.out.println("Insert performance ... , number: " + testNum);
        long cTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            MyTest test = new MyTest();

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
        for (MyTest tmp : list) {
            dao.getObject(tmp);
        }
        System.out.println("run:" + (System.currentTimeMillis() - cTime) + " ms");
        System.out.println("Update performance ... , number: " + testNum);
        cTime = System.currentTimeMillis();
        for (MyTest tmp : list) {
//            tmp.id = 0;
            tmp.d = 3;
            dao.update(tmp);
        }
        System.out.println("run:" + (System.currentTimeMillis() - cTime) + " ms");
        System.out.println("Delete performance ... , number: " + testNum);
        cTime = System.currentTimeMillis();
        for (MyTest tmp : list) {
            dao.delete(tmp);
        }
        System.out.println("run:" + (System.currentTimeMillis() - cTime) + " ms");
    }

    public static void main(String[] args) throws Exception {
        testPerformance(100);
//        DAO dao = new SQLDAO();
//        Set<String> primary_keys = DBObjectManager.getTablePrimaryKey("myTest");

//        System.out.println(primary_keys);

//        System.out.println("insert 100 run:" +(System.currentTimeMillis() - cTime) + " ms");

    }
}
