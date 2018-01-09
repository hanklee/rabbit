/**
 * Create time: 06-Jan-2018
 */
package com.lixianling.rabbit.sql;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.sql.SQLDAO;
import com.lixianling.rabbit.manager.DBObjectManager;
import com.lixianling.rabbit.manager.DataSourceManager;

import java.util.Set;

/**
 *
 * @author hank
 */
public class MyTest extends DBObject {
    public String table_name = "myTest";

    public int a;
    public int b;
    public int c;
    public int d;

    public static void main(String[] args) {

        DAO dao = new SQLDAO();
        Set<String> primary_keys = DBObjectManager.getTablePrimaryKey("myTest");

        System.out.println(primary_keys);
        MyTest test = new MyTest();

        test.a =1;
        test.b = 3;
        test.c = 4;
        test.d = 5;

        try {
            dao.update(test);
        } catch (DBException e) {
            e.printStackTrace();
        }
    }
}
