/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 22:39
 */
package com.lixianling.rabbit.dao.sql;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.DAOHandler;
import com.lixianling.rabbit.manager.DBObjectManager;
import com.lixianling.rabbit.manager.DataSourceManager;
import com.lixianling.rabbit.manager.RabbitManager;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Xianling Li(hanklee)
 * $Id: SQLDAO.java 39 2016-01-08 12:04:37Z hank $
 */
public class SQLDAO extends DAO {

    private final QueryRunner innerRunner;

    public SQLDAO() {
        super(DataSourceManager.getDefaultName());
        if (RabbitManager.RABBIT_CONFIG.mode.contains("mysql")) {
            innerRunner = DataSourceManager.getQueryRunner();
        } else {
            throw new RuntimeException("must config the mysql datasource in rabbit.xml file.");
        }
    }

    public SQLDAO(String source) {
        super(source);
        if (RabbitManager.RABBIT_CONFIG.mode.contains("mysql")) {
            innerRunner = DataSourceManager.getQueryRunner(source);
        } else {
            throw new RuntimeException("must config the data source in rabbit.xml file.");
        }
    }

    public QueryRunner getQueryRunner() {
        return innerRunner;
    }

    /**
     * update a object's data to database
     *
     * @param obj   an object to update his data to database
     * @param table table name
     * @throws DBException db Exception
     */
    public void update(DBObject obj, String table) throws DBException {
        updateExcludes(obj, table, null);
    }

    /**
     * update a object's data to database
     *
     * @param obj      an object to update his data to database
     * @param table    table name
     * @param excludes do not update columns
     * @throws DBException db operation exception
     */
    public void updateExcludes(DBObject obj, String table, final Set<String> excludes) throws DBException {
        try {
            Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(source, table);
            Set<String> columns = DBObjectManager.getTableAllColumnsNoKey(source, table);
            int rsize = (excludes == null) ? 0 : excludes.size();
            Object[] objs = new Object[columns.size() + primary_keys.size() - rsize];
            int count = 0;
            for (String column : columns) {
                if (excludes == null || !excludes.contains(column)) {
                    objs[count] = obj.getValueByField(column);
                    count++;
                }
            }

            for (String primary_key : primary_keys) {
                objs[count] = obj.getValueByField(primary_key);
                count++;
            }
            String sql = SQLBuilder.getOpUpdate(source, table, excludes);
            obj.beforeUpdate(this, table, innerRunner);
            int mount = innerRunner.update(sql, objs);
            if (mount < 1) {
                throw new SQLException("No data update." + sql + "\n" + obj);
            }
            obj.afterUpdate(this, table, innerRunner);
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
        try {
            obj.beforeInsert(this, table, innerRunner);
            Field keyField = DBObjectManager.getInsertIncrKeyField(source, table, obj);
            Set<String> columns = DBObjectManager.getTableAllColumnsNoIncr(source, table); // true means if it is not auto increase then add key's column
            Object[] objs = new Object[columns.size()];
            int count = 0;
            for (String column : columns) {
                objs[count] = obj.getValueByField(column);
                count++;
            }

            String sql = SQLBuilder.getInsertSQLByTable(source, table);
            Connection conn = innerRunner.getDataSource().getConnection();
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

                innerRunner.fillStatement(stmt, objs);
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
                DbUtils.close(conn);
            }
            obj.afterInsert(this, table, innerRunner);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void update(DBObject obj, String table, String[] fields) throws DBException {
        try {
            Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(source, table);
            Object[] objs = new Object[fields.length + primary_keys.size()];
            int count = 0;
            for (String column : fields) {
                objs[count] = obj.getValueByField(column);
                count++;
            }
            for (String primary_key : primary_keys) {
                objs[count] = obj.getValueByField(primary_key);
                count++;
            }
            String sql = SQLBuilder.makeUpdateWithFieldsSQL(source, table, fields);
            obj.beforeUpdate(this, table, innerRunner);
            int mount = innerRunner.update(sql, objs);
            if (mount < 1) {
                throw new SQLException("No data update." + sql + "\n" + obj);
            }
            obj.afterUpdate(this, table, innerRunner);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void update(String table, Map<String, Object> valueObj, Map<String, Object> whereObj) throws DBException {
        try {
            String[] fields = new String[valueObj.size()];
            String[] whereFields = new String[whereObj.size()];

            fields = valueObj.keySet().toArray(fields);
            whereFields = whereObj.keySet().toArray(whereFields);

            Object[] objs = new Object[fields.length + whereFields.length];
            int count = 0;
            for (Object obj : valueObj.values()) {
                objs[count] = obj;
                count++;
            }

            for (Object obj : whereObj.values()) {
                objs[count] = obj;
                count++;
            }
            String sql = SQLBuilder.makeUpdateWithAllFieldsSQL(table, fields, whereFields);
            int mount = innerRunner.update(sql, objs);
            if (mount < 1) {
                throw new SQLException("No data update." + sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void update(String table, String[] fields, Object[] valueObjs, String[] whereFields, Object[] whereObjs) throws DBException {
        try {
            Object[] objs = new Object[valueObjs.length + whereObjs.length];
            int count = 0;
            for (Object valueObj : valueObjs) {
                objs[count] = valueObj;
                count++;
            }
            for (Object valueObj : whereObjs) {
                objs[count] = valueObj;
                count++;
            }
            String sql = SQLBuilder.makeUpdateWithAllFieldsSQL(table, fields, whereFields);
            int mount = innerRunner.update(sql, objs);
            if (mount < 1) {
                throw new SQLException("No data update." + sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public <T extends DBObject> T getObject(String table, Object... objs) throws DBException {
        String sql = SQLBuilder.getObjectSQLByTable(source, table);
        Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(source, table);
        Class<T> objclazz = DBObjectManager.getClassByTable(source, table);
        if (objclazz == null) {
            throw new DBException("not found table class");
        }
        if (primary_keys.size() == 0) {
            throw new DBException("Not support table has not primary key.");
        }
        try {
            return innerRunner.query(sql, MapToDBObject.newRsHandler(objclazz), objs);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public <T extends DBObject> T getObject(String table, String[] fields, Object... objs) throws DBException {
        String sql = SQLBuilder.makeGetObjectsSQL(table, fields);
        Class<T> objclazz = DBObjectManager.getClassByTable(source, table);
        try {
            return innerRunner.query(sql, MapToDBObject.newRsHandler(objclazz), objs);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public <T extends DBObject> T getObject(String table, Map<String, Object> whereObj) throws DBException {
        String[] fields = new String[whereObj.size()];
        fields = whereObj.keySet().toArray(fields);
        Object[] objs = new Object[fields.length];
        int count = 0;
        for (Object obj : whereObj.values()) {
            objs[count] = obj;
            count++;
        }
        return getObject(table, fields, objs);
    }

    @Override
    public <T extends DBObject> List<T> getObjects(String table, String[] fields, Object... objs) throws DBException {
        String sql = SQLBuilder.makeGetObjectsSQL(table, fields);
        Class<T> objclazz = DBObjectManager.getClassByTable(source, table);
        try {
            return innerRunner.query(sql, MapToDBObject.newListHandler(objclazz), objs);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public <T extends DBObject> List<T> getObjects(String table, Map<String, Object> whereObj) throws DBException {
        String[] fields = new String[whereObj.size()];
        fields = whereObj.keySet().toArray(fields);
        Object[] objs = new Object[fields.length];
        int count = 0;
        for (Object obj : whereObj.values()) {
            objs[count] = obj;
            count++;
        }
        return getObjects(table, fields, objs);
    }

    @Override
    public void deleteObjects(String table, String[] fields, Object... objs) throws DBException {
        String sql = SQLBuilder.makeDeleteObjectsSQL(table, fields);
        int mount;
        try {
            mount = innerRunner.update(sql, objs);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
        if (mount < 1) {
            throw new DBException("No data delete." + sql + "\n" + objs);
        }
    }

    @Override
    public void deleteObjects(String table, Map<String, Object> whereObj) throws DBException {
        String[] fields = new String[whereObj.size()];
        fields = whereObj.keySet().toArray(fields);
        Object[] objs = new Object[fields.length];
        int count = 0;
        for (Object obj : whereObj.values()) {
            objs[count] = obj;
            count++;
        }
        deleteObjects(table, fields, objs);
    }

    /**
     * delete table object's data method
     *
     * @param obj   a Object relate the table's row data
     * @param table table name
     * @throws DBException db operation exception
     */
    public void delete(DBObject obj, String table) throws DBException {
        try {
            Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(source, table);
            Object[] objs = new Object[primary_keys.size()];
            int count = 0;
            for (String primary_key : primary_keys) {
                objs[count] = obj.getValueByField(primary_key);
                count++;
            }
            String sql = SQLBuilder.getDeleteSQLByTable(source, table);
            obj.beforeDelete(this, table, innerRunner);
            int mount = innerRunner.update(sql, objs);
            if (mount < 1) {
                throw new SQLException("No data delete." + sql + "\n" + obj);
            }
            obj.afterDelete(this, table, innerRunner);
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

    /**
     * sql Transaction handle
     *
     * @param daoHandler implement implements SQLDAOTransitionHandler
     * @param <T>        template class
     * @return obj
     * @throws DBException db exception
     */
    public <T> T executeTransaction(final SQLDAOTransitionHandler<T> daoHandler) throws DBException {
        return new SQLDAOTransitionExecute<T>(this.innerRunner) {
            @Override
            T execute(Connection conn) throws DBException {
                return daoHandler.handle(conn);
            }
        }.run();
    }

    public void update(QueryRunner queryRunner, Collection<? extends DBObject> objs, String table) throws DBException {
        try {
            Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(source, table);
            Set<String> columns = DBObjectManager.getTableAllColumnsNoKey(source, table);

            String sql = SQLBuilder.getUpdateSQLByTable(source, table);
            Object[][] data = new Object[objs.size()][];
            int data_index = 0;
            for (DBObject obj : objs) {
                obj.beforeUpdate(this, table, queryRunner);
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
                obj.afterUpdate(this, table, queryRunner);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBException(e.getMessage());
        }
    }


    public void insert(QueryRunner queryRunner, Collection<? extends DBObject> objs, String table) throws DBException {
        try {
            Set<String> columns = DBObjectManager.getTableAllColumnsNoIncr(source, table); // true means if it is not auto increase then add key's column
            String sql = SQLBuilder.getInsertSQLByTable(source, table);
            Object[][] data = new Object[objs.size()][];
            int data_index = 0;
            for (DBObject obj : objs) {
                obj.beforeInsert(this, table, queryRunner);
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
                obj.afterInsert(this, table, queryRunner);
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
            Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(source, table);
            String sql = SQLBuilder.getDeleteSQLByTable(source, table);
            Object[][] keys = new Object[objs.size()][];

            int data_index = 0;
            for (DBObject obj : objs) {
                obj.beforeDelete(this, table, queryRunner);
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
                obj.beforeDelete(this, table, queryRunner);
            }
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    /*
         TRANSACTION METHOD

     */

    public void update(Connection conn, DBObject obj) throws DBException {
        update(conn, obj, obj.getTableName());
    }

    public void update(Connection conn, DBObject obj, String table) throws DBException {
        SQLTransaction.update(this, conn, obj, table);
    }


    public void insert(Connection conn, DBObject obj) throws DBException {
        insert(conn, obj, obj.getTableName());
    }

    public void insert(Connection conn, DBObject obj, String table) throws DBException {
        SQLTransaction.insert(this, conn, obj, table);
    }

    public void delete(Connection conn, DBObject obj) throws DBException {
        delete(conn, obj, obj.getTableName());
    }

    public void delete(Connection conn, DBObject obj, String table) throws DBException {
        SQLTransaction.delete(this, conn, obj, table);
    }
}
