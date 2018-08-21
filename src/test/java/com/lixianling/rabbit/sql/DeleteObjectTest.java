/**
 * Create time: 2018-08-21
 */
package com.lixianling.rabbit.sql;

import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.sql.SQLDAO;
import com.lixianling.rabbit.manager.RabbitManager;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Xianling Li
 */
public class DeleteObjectTest extends TestCase {

    public void testDeleteObj1() throws Exception {
        RabbitManager.register(); // need register to load config file and initiate
        DAO dao = new SQLDAO();

        List<MyTest.MyTestData> list = new ArrayList<MyTest.MyTestData>();
        for (int i = 0; i < 20; i++) {
            MyTest.MyTestData test = new MyTest.MyTestData();

            test.a = 1;
            test.b = 3;
            test.c = 4;
            test.d = 5;
            test.a = i + 203;
            dao.insert(test);
            list.add(test);
        }

        Map<String, Object> whereObjs = new HashMap<String, Object>();
        whereObjs.put("b", 3);
        dao.deleteObjects("myTest", whereObjs);

        List<MyTest.MyTestData> list3 = dao.getObjects("myTest", whereObjs);
        Assert.assertEquals(0, list3.size());
    }

    public void testDeleteObj2() throws Exception {
        RabbitManager.register(); // need register to load config file and initiate
        DAO dao = new SQLDAO();

        List<MyTest.MyTestData> list = new ArrayList<MyTest.MyTestData>();
        for (int i = 0; i < 20; i++) {
            MyTest.MyTestData test = new MyTest.MyTestData();

            test.a = 1;
            test.b = 3;
            test.c = 4;
            test.d = 5;
            test.a = i + 203;
            dao.insert(test);
            list.add(test);
        }

        Map<String, Object> whereObjs = new HashMap<String, Object>();
        whereObjs.put("b", 3);
        dao.deleteObjects("myTest", new String[]{"b"}, 3);

        List<MyTest.MyTestData> list3 = dao.getObjects("myTest", whereObjs);
        Assert.assertEquals(0, list3.size());

    }
}
