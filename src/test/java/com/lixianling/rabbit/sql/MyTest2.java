/**
 * Create time: 2018-07-20
 */
package com.lixianling.rabbit.sql;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.sql.SQLDAO;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xianling Li
 */
public class MyTest2 extends TestCase {

    public void testAdd(){
        DAO dao = new SQLDAO();
        MyTestData2 test = new MyTestData2();

        test.a = 1;
        test.b = 3;
        test.c = 4;
        test.d = 5;
        test.a = 203;
        try {
            dao.insert(test);
            System.out.println(test.toDBJson().toString());
            dao.delete(test);
        } catch (DBException e) {
            e.printStackTrace();
        }

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
        List<MyTestData2> list = new ArrayList<MyTestData2>();
        System.out.println("Insert performance ... , number: " + testNum);
        long cTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            MyTestData2 test = new MyTestData2();

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
        for (MyTestData2 tmp : list) {
            dao.getObject(tmp);
        }
        System.out.println("run:" + (System.currentTimeMillis() - cTime) + " ms");
        System.out.println("Update performance ... , number: " + testNum);
        cTime = System.currentTimeMillis();
        for (MyTestData2 tmp : list) {
//            tmp.id = 0;
            tmp.d = 3;
            dao.update(tmp);
        }
        System.out.println("run:" + (System.currentTimeMillis() - cTime) + " ms");
        System.out.println("Delete performance ... , number: " + testNum);
        cTime = System.currentTimeMillis();
        for (MyTestData2 tmp : list) {
            dao.delete(tmp);
        }
        System.out.println("run:" + (System.currentTimeMillis() - cTime) + " ms");
    }


    public static class MyTestData2 extends DBObject {
        public String table_name = "myTest";

        private int a;
        private int b;
        private int c;
        private int d;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        public int getC() {
            return c;
        }

        public void setC(int c) {
            this.c = c;
        }

        public int getD() {
            return d;
        }

        public void setD(int d) {
            this.d = d;
        }
    }

}
