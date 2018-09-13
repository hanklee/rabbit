/**
 * Create time: 2018-09-13
 */
package com.lixianling.rabbit.sql;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.DAOHandler;
import com.lixianling.rabbit.dao.sql.MapToDBObject;
import com.lixianling.rabbit.dao.sql.SQLDAO;
import com.lixianling.rabbit.dao.sql.SQLDAOTransitionHandler;
import com.lixianling.rabbit.manager.RabbitManager;
import junit.framework.TestCase;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.Assert;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Xianling Li
 */
public class TransitionTest extends TestCase {

    public void test_v1() {
        RabbitManager.register();
        final DAO dao = new SQLDAO();
        final MyTest3.TestData7 td7 = new MyTest3.TestData7();
        td7.setName("name");
        td7.setName2("name2");

        try {
            ((SQLDAO) dao).executeTransaction(new SQLDAOTransitionHandler<Void>() {
                @Override
                public Void handle(Connection conn) throws DBException {
                    ((SQLDAO) dao).insert(conn, td7);
                    return null;
                }
            });
        } catch (DBException e) {
            e.printStackTrace();
        }


        DAOHandler<MyTest3.TestData7> getData = new DAOHandler<MyTest3.TestData7>() {
            @Override
            public MyTest3.TestData7 handle(Object con) throws DBException {
                QueryRunner qRunner = (QueryRunner) con;
                String sql = "SELECT test1.* , test2.name2 FROM `test2` LEFT JOIN test1 ON test2.id = test1.id WHERE `test1`.`id` = ? ";
                try {
                    return qRunner.query(sql, MapToDBObject.newRsHandler(MyTest3.TestData7.class), td7.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new DBException(e.getSQLState());
                }
            }
        };

        try {
            MyTest3.TestData7 nData = dao.execute(getData);
            Assert.assertEquals(td7.getId(), nData.getId());
            Assert.assertEquals(td7.getName(), nData.getName());
            Assert.assertEquals(td7.getName2(), nData.getName2());
            dao.delete(nData);
        } catch (DBException e) {
            e.printStackTrace();
        }
    }
}
