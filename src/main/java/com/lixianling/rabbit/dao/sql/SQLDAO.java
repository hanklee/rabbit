/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 22:39
 */
package com.lixianling.rabbit.dao.sql;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.conf.RabbitConfig;
import com.lixianling.rabbit.dao.DAOHandler;
import com.lixianling.rabbit.manager.DBObjectManager;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.manager.DataSourceManager;
import com.lixianling.rabbit.manager.RabbitManager;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: SQLDAO.java 39 2016-01-08 12:04:37Z hank $
 */
public class SQLDAO extends DAO {

    private QueryRunner innerRunner;

    public SQLDAO(){
        if (RabbitManager.RABBIT_CONFIG.mode.contains("mysql")) {
            setQueryRunner(DataSourceManager.getQueryRunner());
        } else {
            throw new RuntimeException("must config the data source in rabbit.xml file.");
        }
    }

    public SQLDAO(QueryRunner queryRunner) {
        setQueryRunner(queryRunner);
    }

    public void setQueryRunner(QueryRunner queryRunner) {
        innerRunner = queryRunner;
    }


    /**
     * update a object's data to database
     *
     * @param obj   an object to update his data to database
     * @param table table name
     * @throws DBException db Exception
     */
    public void update(DBObject obj, String table) throws DBException {
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
            obj.beforeUpdate(innerRunner);
            int mount = innerRunner.update(sql, objs);
            if (mount < 1) {
//                System.err.println(map);
//                System.err.println(DBObjectHelper.getTableAllColumns(table));
                throw new SQLException("No data update." + sql + "\n" + obj);
            }
            obj.afterUpdate(innerRunner);
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
    public void insert(DBObject obj, String table) throws DBException {
//        obj.generateId(table, idGen);
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
            // no thread safe
            obj.beforeInsert(innerRunner);
            int mount = 0; // innerInsertRunner.insert(obj, keyField, sql, objs);
            Connection conn = innerRunner.getDataSource().getConnection();
            PreparedStatement stmt = null;
            int rows = 0;
            ResultSet autoKeyRs = null;
            // Clear generatedKeys first, in case an exception is thrown
            try {
                stmt = conn.prepareStatement(sql);
                innerRunner.fillStatement(stmt, objs);
                rows = stmt.executeUpdate();
                autoKeyRs = stmt.getGeneratedKeys();
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
                DbUtils.close(conn);
            }

            if (mount < 1) {
                throw new SQLException("No data insert." + sql + "\n" + obj);
            }
            obj.afterInsert(innerRunner);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public DBObject getObject(DBObject obj,String table) throws DBException {
        String sql = SQLBuilder.getObjectSQLByTable(table);
        Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(table);
        if (primary_keys.size() == 0) {
            throw new DBException("Not support table has not primary key.");
        }
        Object[] objs = new Object[primary_keys.size()];
        int count = 0;
        for (String primary_key : primary_keys) {
            objs[count] = obj.getValueByField(primary_key);
            count++;
        }
        try {
            return innerRunner.query(sql,MapToDBObject.newRsHandler(obj.getClass()),objs);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    /**
     * delete table object's data method
     *
     * @param obj   a Object relate the table's row data
     * @param table table name
     * @throws DBException
     */
    public void delete(DBObject obj, String table) throws DBException {
        try {
            Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(table);
            Object[] objs = new Object[primary_keys.size()];
            int count = 0;
            for (String primary_key : primary_keys) {
                objs[count] = obj.getValueByField(primary_key);
                count++;
            }
            String sql = SQLBuilder.getDeleteSQLByTable(table);
            obj.beforeDelete(innerRunner);
            int mount = innerRunner.update(sql, objs);
            if (mount < 1) {
                throw new SQLException("No data delete." + sql + "\n" + obj);
            }
            obj.afterDelete(innerRunner);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }


    /*

      BATCH OPERATION

     */

    public void update(Collection<? extends DBObject> objs, String table_name) throws DBException {
        update(innerRunner, objs, table_name);
    }

    public void insert(Collection<? extends DBObject> objs, String table_name) throws DBException {
        insert(innerRunner, objs, table_name);
    }

    public void delete(Collection<? extends DBObject> objs, String table_name) throws DBException {
        delete(innerRunner, objs, table_name);
    }

    @Override
    public <T> T execute(final DAOHandler<T> daoHandler) throws DBException {
        return daoHandler.handle(this.innerRunner);
    }

    public void update(QueryRunner queryRunner, Collection<? extends DBObject> objs, String table) throws DBException {
        try {
            Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(table);
            Set<String> columns = DBObjectManager.getTableAllColumnsNoKey(table);

            String sql = SQLBuilder.getUpdateSQLByTable(table);
            Object[][] data = new Object[objs.size()][];
            int data_index = 0;
            for (DBObject obj : objs) {
                obj.beforeUpdate(queryRunner);
                Object[] objss = new Object[columns.size() + primary_keys.size()];
                int count = 0;
                for (String column : columns) {
                    objss[count] = obj.getValueByField(column);
                    count++;
                }
                for (String primary_key : primary_keys) {
                    objss[count] = obj.getValueByField(primary_key);
                    count++;
                }
                data[data_index] = objss;
                data_index++;
            }
            queryRunner.batch(sql, data);
            for (DBObject obj : objs) {
                obj.afterUpdate(queryRunner);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBException(e.getMessage());
        }
    }


    public void insert(QueryRunner queryRunner, Collection<? extends DBObject> objs, String table) throws DBException {
        try {
            Set<String> columns = DBObjectManager.getTableAllColumnsNoIncr(table); // true means if it is not auto increase then add key's column
            String sql = SQLBuilder.getInsertSQLByTable(table);
            Object[][] data = new Object[objs.size()][];
            int data_index = 0;
            for (DBObject obj : objs) {
                obj.beforeInsert(queryRunner);
                Object[] objss = new Object[columns.size()];
                int count = 0;
                for (String column : columns) {
                    objss[count] = obj.getValueByField(column);
                    count++;
                }
                data[data_index] = objss;
                data_index++;
            }
            queryRunner.batch(sql, data);
            for (DBObject obj : objs) {
                obj.afterInsert(queryRunner);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBException(e.getMessage());
        }
    }

    /**
     * delete table object's data method
     *
     * @param queryRunner dbutils QueryRunner class
     * @param objs        a Object relate the table's row data
     * @param table       table name
     * @throws DBException
     */
    public void delete(QueryRunner queryRunner, Collection<? extends DBObject> objs, String table) throws DBException {
        try {
            Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(table);
            String sql = SQLBuilder.getDeleteSQLByTable(table);
            Object[][] keys = new Object[objs.size()][];

            int data_index = 0;
            for (DBObject obj : objs) {
                obj.beforeDelete(queryRunner);
                Object[] objss = new Object[primary_keys.size()];
                int count = 0;
                for (String column : primary_keys) {
                    objss[count] = obj.getValueByField(column);
                    count++;
                }
                keys[data_index] = objss;
                data_index++;
            }

            queryRunner.batch(sql, keys);
            for (DBObject obj : objs) {
                obj.beforeDelete(queryRunner);
            }
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    /*
         TRANSACTION METHOD

     */

    public void update(Connection conn,DBObject obj) throws DBException {
        update(innerRunner,conn,obj,obj.getTableName());
    }

    public void update(QueryRunner queryRunner, Connection conn,DBObject obj) throws DBException {
        update(queryRunner,conn,obj,obj.getTableName());
    }

    public void update(QueryRunner queryRunner, Connection conn,DBObject obj, String table) throws DBException {
        SQLTransaction.update(queryRunner,conn,obj,table);
    }

    public void insert(Connection conn,DBObject obj) throws DBException {
        insert(innerRunner,conn,obj,obj.getTableName());
    }

    public void insert(QueryRunner queryRunner, Connection conn,DBObject obj) throws DBException {
        insert(queryRunner,conn,obj,obj.getTableName());
    }

    public void insert(QueryRunner queryRunner, Connection conn,DBObject obj, String table) throws DBException {
        SQLTransaction.insert(queryRunner,conn,obj,table);
    }

    public void delete(Connection conn,DBObject obj) throws DBException {
        delete(innerRunner,conn,obj,obj.getTableName());
    }

    public void delete(QueryRunner queryRunner, Connection conn,DBObject obj) throws DBException {
        delete(queryRunner,conn,obj,obj.getTableName());
    }

    public void delete(QueryRunner queryRunner, Connection conn,DBObject obj, String table) throws DBException {
        SQLTransaction.delete(queryRunner,conn,obj,table);
    }
}
