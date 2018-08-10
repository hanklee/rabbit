/**
 * Create time: 11-Jan-2018
 */
package com.lixianling.rabbit.dao.sql;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.dao.DAOExecute;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;


import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author hank
 */
public abstract class SQLExecute<T> implements DAOExecute<T> {
    private QueryRunner innerRunner;

    public SQLExecute(QueryRunner queryRunner) {
        this.innerRunner = queryRunner;
    }

    public T run() throws DBException {
        Connection conn = null;
        try {
            try {
                conn = innerRunner.getDataSource().getConnection();
                conn.setAutoCommit(false);
                T result = execute(conn);
                conn.commit();
                return result;
            } catch (SQLException e) {
                if (conn != null) {
                    conn.rollback();
                }
                throw new DBException(e.getSQLState());
            } catch (DBException e) {
                conn.rollback();
                throw e;
            } catch (Exception e) {
                if (conn != null) {
                    conn.rollback();
                }
                throw new DBException(e.getMessage());
            } finally {
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        DbUtils.close(conn);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (DBException e) {
            throw e;
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }
}
