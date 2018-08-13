/**
 * Create time: 2018-07-23
 */
package com.lixianling.rabbit.obj;

import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.sql.SQLDAO;
import com.lixianling.rabbit.manager.RabbitManager;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

import static com.lixianling.rabbit.obj.TestObj2.printFieldInfos;

/**
 * @author Xianling Li
 */
public class TestObj4 extends TestCase {

    public void test_v1(){
//        List<FieldInfo> fieldInfoList = TypeUtils.computeGetters(TestData5.class, null);
//        printFieldInfos(fieldInfoList);
        RabbitManager.register();
        TestData5 t5 = new TestData5();
        t5.setName("test");
        DAO dao = new SQLDAO();
        try {
            dao.insert(t5);
            System.out.println("insert Id:" + t5.getId());
            Assert.assertEquals(true,t5.getId() > 0);

            dao.delete(t5);
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    public static class TestData4 extends DBObject {
        private String table_name = "test1";
        private int id;

        public String getTable_name() {
            return table_name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

    }

    public static class TestData5 extends TestData4 {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
