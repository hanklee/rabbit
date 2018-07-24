/**
 * Create time: 2018-07-23
 */
package com.lixianling.rabbit.table;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.sql.SQLDAO;
import com.lixianling.rabbit.manager.DBObjectManager;
import junit.framework.TestCase;

import java.util.Set;

/**
 * @author Xianling Li
 */
public class InsertTableTest extends TestCase {

    public void test_v1(){
        Set<String> primary_keys = DBObjectManager.getTablePrimaryKey("test1");
        Set<String> columns = DBObjectManager.getTableAllColumnsNoIncr("test1");
        System.out.println(primary_keys);
        System.out.println(columns);
        InsertData testData = new InsertData();
        testData.setName("insert data");
        DAO dao = new SQLDAO();
        try {
            dao.insert(testData);

            dao.delete(testData);
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    public static class InsertData extends DBObject {
        private String table_name = "test1";
        private int id;
        private String name;

        public String getTable_name() {
            return table_name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
