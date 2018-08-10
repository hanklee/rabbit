/**
 * Create time: 2018-07-23
 */
package com.lixianling.rabbit.table;

import com.lixianling.rabbit.DBObject;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 *
 * 设置table_name 的 3 种方法
 *
 * 填写变量table_name
 * 填写xml <table_name mark="true"></table_name>
 * 类名字 + s 与table_name 一致
 * @author Xianling Li
 */
public class TableNameTest extends TestCase {

    public void test_v1() {
        TableNameObj tObj = new TableNameObj();
        Assert.assertEquals("test_table", tObj.getTableName());
//        Map<String, Field> fieldMap = tObj.getAllFields();
//        for (String key : fieldMap.keySet()) {
//            System.out.println("fieldName:" + key);
//        }

        TableNameObj2 tObj2 = new TableNameObj2();
//        fieldMap = tObj2.getAllFields();
//        for (String key : fieldMap.keySet()) {
//            System.out.println("fieldName:" + key);
//        }
        Assert.assertEquals("test_table", tObj2.getTableName());
    }

    public void test_v2() {
        TableNameObj3 tObj3 = new TableNameObj3();
        Assert.assertEquals("test_table", tObj3.getTableName());

        tObj3.setTable_name("test_table3");
        Assert.assertEquals("test_table3", tObj3.getTableName());
    }

    public void test_v3() {
//        System.out.println(DBObjectManager.getClassByTable("test2"));
//        System.out.println(DBObjectManager.getTableNameByObject(TableNameObj4.class));
        TableNameObj4 tObj4 = new TableNameObj4();
        Assert.assertEquals("test2", tObj4.getTableName());
    }

    /*

    修改对象类相关对应的表(Table) 的两种方法:

     */

    public static class TableNameObj extends DBObject {
        public String table_name = "test_table";
    }

    public static class TableNameObj2 extends DBObject {
        private String table_name = "test_table";

        public String getTable_name() {
            return table_name;
        }
    }

    public static class TableNameObj3 extends DBObject {
        private String table_name = "test_table";

        public String getTable_name() {
            return table_name;
        }

        public void setTable_name(String table_name) {
            this.table_name = table_name;
        }
    }

    public static class TableNameObj4 extends DBObject {
    }

}
