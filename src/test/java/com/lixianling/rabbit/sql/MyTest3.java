/**
 * Create time: 2018-07-23
 */
package com.lixianling.rabbit.sql;

import com.lixianling.rabbit.obj.TestObj4;
import junit.framework.TestCase;

/**
 * @author Xianling Li
 */
public class MyTest3 extends TestCase {


    public void test_v1(){

    }

    /**
     *
     *  两个类，跨表读取数据 id 是两个表的连接
     *
     */

    public static class TestData6 extends TestObj4.TestData5 {
        private String table_name = "test2";

        @Override
        public String getTable_name() {
            return table_name;
        }

        private String name2;

        public String getName2() {
            return name2;
        }

        public void setName2(String name2) {
            this.name2 = name2;
        }
    }
}
