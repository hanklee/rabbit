/**
 * Create time: 2018-04-04
 */
package com.lixianling.rabbit.dao.sql;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.manager.DBObjectManager;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * @author Xianling Li
 */
public final class SQLTransaction {

    /**
     * update a object's data to database
     *
     * @param obj   an object to update his data to database
     * @param table table name
     * @throws DBException db Exception
     */
    public static void update(QueryRunner queryRunner, Connection con, DBObject obj, String table) throws DBException {
        try {
            Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(table);
            Set<String> columns = DBObjectManager.getTableAllColumnsNoKey(table);
            Object[] objs = new Object[columns.size() + primary_keys.size()];
            int count = 0;
            for (String column : columns) {
                objs[count] = obj.getValueByField(column);
                count++;
            }

            for (String primary_key : primary_keys) {
                objs[count] = obj.getValueByField(primary_key);
                count++;
            }
            String sql = SQLBuilder.getUpdateSQLByTable(table);
            obj.beforeUpdate(queryRunner);
            int mount = queryRunner.update(con, sql, objs);
            if (mount < 1) {
//                System.err.println(map);
//                System.err.println(DBObjectHelper.getTableAllColumns(table));
                throw new SQLException("No data update." + sql + "\n" + obj);
            }
            obj.afterUpdate(queryRunner);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBException(e.getMessage());
        }
    }

    /**
     * insert a object's data to database
     *
     * @param obj   an object to insert his data to database
     * @param table table name
     * @throws DBException db Exception
     */
    public static void insert(QueryRunner queryRunner, Connection conn, DBObject obj, String table) throws DBException {
        try {
            Field keyField = DBObjectManager.getInsertIncrKeyField(table);
            Set<String> columns = DBObjectManager.getTableAllColumnsNoIncr(table); // true means if it is not auto increase then add key's column
            Object[] objs = new Object[columns.size()];
            int count = 0;
            for (String column : columns) {
                objs[count] = obj.getValueByField(column);
                count++;
            }
            String sql = SQLBuilder.getInsertSQLByTable(table);
            obj.beforeInsert(queryRunner);
            //int mount = queryRunner.insert(con,obj, keyField, sql, objs);
            PreparedStatement stmt = null;
            int rows = 0;
            ResultSet autoKeyRs = null;
            // Clear generatedKeys first, in case an exception is thrown
            try {
                // if (keyField != null) {
                //     stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                // } else {
                //     stmt = conn.prepareStatement(sql);
                // }
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                queryRunner.fillStatement(stmt, objs);
                rows = stmt.executeUpdate();
                autoKeyRs = stmt.getGeneratedKeys();
                if (rows < 1) {
                    throw new SQLException("No data insert." + sql + "\n" + obj);
                }
                if (rows == 1 && keyField != null) {
                    Long generatedKeys = new ScalarHandler<Long>().handle(autoKeyRs);
                    if (keyField.getType().equals(Integer.TYPE)) {
                        keyField.set(obj, generatedKeys.intValue());
                    } else if (keyField.getType().equals(Long.TYPE)) {
                        keyField.set(obj, generatedKeys);
                    }
                }
            } catch (SQLException e) {
                throw e;
            } catch (Exception e) {
                throw new SQLException(e.getMessage());
            } finally {
                DbUtils.close(autoKeyRs);
                DbUtils.close(stmt);
            }
            obj.afterInsert(queryRunner);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBException(e.getMessage());
        }
    }

    public static void delete(QueryRunner queryRunner, Connection con, DBObject obj, String table) throws DBException {
        try {
            Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(table);
            Object[] objs = new Object[primary_keys.size()];
            int count = 0;
            for (String primary_key : primary_keys) {
                objs[count] = obj.getValueByField(primary_key);
                count++;
            }
            String sql = SQLBuilder.getDeleteSQLByTable(table);
            obj.beforeDelete(queryRunner);
            int mount = queryRunner.update(con, sql, objs);
            if (mount < 1) {
                throw new SQLException("No data delete." + sql + "\n" + obj);
            }
            obj.afterDelete(queryRunner);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

}
