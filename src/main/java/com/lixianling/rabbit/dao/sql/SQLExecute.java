/**
 * Create time: 11-Jan-2018
 */
package com.lixianling.rabbit.dao.sql;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.dao.DAOExecute;
import org.apache.commons.dbutils.QueryRunner;
import redis.clients.jedis.Jedis;

/**
 *
 * @author hank
 */
public abstract class SQLExecute<T> implements DAOExecute<T> {
    private QueryRunner innerRunner;

    public SQLExecute(QueryRunner queryRunner) {
        this.innerRunner = queryRunner;
    }

    public T run() throws DBException {
        try {
            return execute(this.innerRunner);
        } catch (DBException e) {
            throw e;
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }
}
