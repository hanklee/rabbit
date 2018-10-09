/**
 * Create time: 2018-10-09
 */
package com.lixianling.rabbit.sql;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.sql.SQLDAO;
import com.lixianling.rabbit.manager.RabbitManager;
import junit.framework.TestCase;
import org.junit.Assert;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Xianling Li
 */
public class DateObjectTest extends TestCase {

    public void testAdd() {
        RabbitManager.register(); // need register to load config file and initiate
        DAO dao = new SQLDAO();
        MyTestDate test = new MyTestDate();
        test.setCreate_at(new Date());
        test.setCreate_at2(new java.sql.Timestamp(System.currentTimeMillis()));

        try {
            dao.insert(test);
            MyTestDate tmp = dao.getObject("myDate",test.getId());
            Assert.assertNotNull(tmp.getCreate_at());
            dao.delete(test);
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    public static class MyTestDate extends DBObject {
        private int id;
        private Date create_at;
        private java.sql.Timestamp create_at2;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Date getCreate_at() {
            return create_at;
        }

        public void setCreate_at(Date create_at) {
            this.create_at = create_at;
        }

        public Timestamp getCreate_at2() {
            return create_at2;
        }

        public void setCreate_at2(Timestamp create_at2) {
            this.create_at2 = create_at2;
        }
    }
}
