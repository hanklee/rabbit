/**
 * Create time: 2018-08-11
 */
package com.lixianling.rabbit.sql;

import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.sql.SQLDAO;
import com.lixianling.rabbit.manager.RabbitManager;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xianling Li
 */
public class GetObjectTest extends TestCase {

    public void testGetobjs() throws Exception {
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

        List<MyTest.MyTestData> list2 = dao.getObjects("myTest", new String[]{"b"}, 3);
        Assert.assertEquals(list.size(),list2.size());

        for (MyTest.MyTestData tmp : list2) {
            dao.delete(tmp);
        }

    }

}
