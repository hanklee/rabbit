/**
 * Create time: 2018-07-23
 */
package com.lixianling.rabbit.sql;

import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.DAOHandler;
import com.lixianling.rabbit.dao.sql.MapToDBObject;
import com.lixianling.rabbit.dao.sql.SQLDAO;
import com.lixianling.rabbit.obj.TestObj4;
import junit.framework.TestCase;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.Assert;

import java.sql.SQLException;
import java.util.List;

import static com.lixianling.rabbit.obj.TestObj2.printFieldInfos;

/**
 *
 *  两个继承类(表)的操作
 *
 * @author Xianling Li
 */
public class MyTest3 extends TestCase {


    public void test_v1() {
        List<FieldInfo> fieldInfoList = TypeUtils.computeGetters(TestData6.class, null);
        printFieldInfos(fieldInfoList);
        DAO dao = new SQLDAO();
        final TestData6 td6 = new TestData6();
        td6.setName("name");
        td6.setName2("name2");
        try {
            dao.insert(td6, "test1"); // 插入父类数据
            dao.insert(td6);
        } catch (DBException e) {
            e.printStackTrace();
        }

        DAOHandler<TestData6> getData = new DAOHandler<TestData6>() {
            @Override
            public TestData6 handle(Object con) throws DBException {
                QueryRunner qRunner = (QueryRunner) con;
                String sql = "SELECT test1.* , test2.name2 FROM `test2` LEFT JOIN test1 ON test2.id = test1.id WHERE `test1`.`id` = ? ";
                try {
                    return qRunner.query(sql, MapToDBObject.newRsHandler(TestData6.class), td6.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new DBException(e.getSQLState());
                }
            }
        };

        try {
            TestData6 nData = dao.execute(getData);
            Assert.assertEquals(td6.getId(), nData.getId());
            Assert.assertEquals(td6.getName(), nData.getName());
            Assert.assertEquals(td6.getName2(), nData.getName2());
            dao.delete(nData, "test1");
            dao.delete(nData);
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    public void test_v2() {
        DAO dao = new SQLDAO();
        final TestData7 td7 = new TestData7();
        td7.setName("name");
        td7.setName2("name2");
        try {
            dao.insert(td7);
        } catch (DBException e) {
            e.printStackTrace();
        }

        DAOHandler<TestData7> getData = new DAOHandler<TestData7>() {
            @Override
            public TestData7 handle(Object con) throws DBException {
                QueryRunner qRunner = (QueryRunner) con;
                String sql = "SELECT test1.* , test2.name2 FROM `test2` LEFT JOIN test1 ON test2.id = test1.id WHERE `test1`.`id` = ? ";
                try {
                    return qRunner.query(sql, MapToDBObject.newRsHandler(TestData7.class), td7.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new DBException(e.getSQLState());
                }
            }
        };

        try {
            TestData7 nData = dao.execute(getData);
            Assert.assertEquals(td7.getId(), nData.getId());
            Assert.assertEquals(td7.getName(), nData.getName());
            Assert.assertEquals(td7.getName2(), nData.getName2());
            dao.delete(nData);
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    /**
     * 两个类，跨表读取数据 id 是两个表的连接
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

    public static class TestData7 extends TestObj4.TestData5 {
        private String table_name = "test2";

        @Override
        public String getTable_name() {
            return table_name;
        }

        @Override
        public void beforeInsert(DAO dao, String table, Object ignored) throws DBException {
            if (!table.equals("test1")) {
                dao.insert(this, "test1");
            }
        }

        @Override
        public void afterDelete(DAO dao, String table, Object ignored) throws DBException {
            if (!table.equals("test1")) {
                dao.delete(this, "test1");
            }
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
