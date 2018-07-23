/**
 * Create time: 2018-07-23
 */
package com.lixianling.rabbit.table;

import com.lixianling.rabbit.DBObject;
import junit.framework.TestCase;
import org.junit.Assert;

import java.lang.reflect.Field;
import java.util.Map;

/**
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

}
