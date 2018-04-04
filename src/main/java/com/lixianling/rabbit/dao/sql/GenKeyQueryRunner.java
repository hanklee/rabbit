/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 22:41
 */
package com.lixianling.rabbit.dao.sql;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import javax.sql.DataSource;

import com.lixianling.rabbit.DBObject;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * @author Xianling Li(hanklee)
 * $Id: GenKeyQueryRunner.java 36 2016-01-06 17:24:04Z hank $
 */
public class GenKeyQueryRunner extends QueryRunner {

    /**
     * The indexes of the key columns which are used to auto-generated key retrieval.
     * May be empty if JDBC should decide on its own which columns suit best.
     * <p/>
     * If this field is not null, then keyColsByName must be null
     */
    private final int[] keyColsByIndex;

    /**
     * The names of the key columns which are used to auto-generated key retrieval.
     * May be empty if JDBC should decide on its own which columns suit best.
     * <p/>
     * If this field is not null, then keyColsByIndex must be null
     */
    private final String[] keyColsByName;

    /**
     * The ResultSetHandler used to transform the generated keys into a Java Object
     */
    private final ResultSetHandler<Long> keyHandler;

    /**
     * The generated keys (available only after a sucessfull call to update())
     */
    // private Long generatedKeys;

    /**
     * Private constructor called to set fields appropriatly
     *
     * @param ds             The <code>DataSource</code> to retrieve connections from.
     *                       and if it breaks, we'll remember not to use it again. See {@link QueryRunner#QueryRunner(boolean)}
     * @param keyColsByIndex The indexes of the key columns which are used to auto-generated key retrieval.
     *                       May be empty if JDBC should decide on its own which columns suit best.
     * @param keyColsByName  The names of the key columns which are used to auto-generated key retrieval.
     *                       May be empty if JDBC should decide on its own which columns suit best.
     * @param keyHandler     The ResultSetHandler used to transform the generated keys into a Java Object
     */
    private GenKeyQueryRunner(DataSource ds,
                              int[] keyColsByIndex, String[] keyColsByName, ResultSetHandler<Long> keyHandler) {
        super(ds);
        this.keyColsByIndex = keyColsByIndex;
        this.keyColsByName = keyColsByName;
        this.keyHandler = keyHandler;
    }

    public GenKeyQueryRunner(QueryRunner qRunner, ResultSetHandler<Long> keyHandler) {
        this(qRunner.getDataSource(), null, null, keyHandler);
    }

    /**
     * Construct a new GenKeyQueryRunner using the given handler,
     * which will let JDBC decide on its own the key columns to use for auto-generated keys retrieval
     *
     * @param keyHandler The ResultSetHandler used to transform the generated keys into a Java Object
     */
    public GenKeyQueryRunner(ResultSetHandler<Long> keyHandler) {
        this(null, null, null, keyHandler);
    }

    /**
     * Construct a new GenKeyQueryRunner using the given handler,
     * which will use the given column indexes for auto-generated keys retrieval
     *
     * @param keyHandler     The ResultSetHandler used to transform the generated keys into a Java Object
     * @param keyColsByIndex The indexes of the key columns which are used to auto-generated key retrieval.
     *                       May be empty if JDBC should decide on its own which columns suit best.
     */
    public GenKeyQueryRunner(ResultSetHandler<Long> keyHandler, int... keyColsByIndex) {
        this(null, keyColsByIndex, null, keyHandler);
    }

    /**
     * Construct a new GenKeyQueryRunner using the given handler,
     * which will use the given column indexes for auto-generated keys retrieval
     *
     * @param keyHandler    The ResultSetHandler used to transform the generated keys into a Java Object
     * @param keyColsByName The names of the key columns which are used to auto-generated key retrieval.
     *                      May be empty if JDBC should decide on its own which columns suit best.
     */
    public GenKeyQueryRunner(ResultSetHandler<Long> keyHandler, String... keyColsByName) {
        this(null, null, keyColsByName, keyHandler);
    }

    /**
     * Construct a new GenKeyQueryRunner using the given handler,
     * which will let JDBC decide on its own the key columns to use for auto-generated keys retrieval
     *
     * @param ds         The <code>DataSource</code> to retrieve connections from.
     * @param keyHandler The ResultSetHandler used to transform the generated keys into a Java Object
     */
    public GenKeyQueryRunner(DataSource ds, ResultSetHandler<Long> keyHandler) {
        this(ds, null, null, keyHandler);
    }

    /**
     * Construct a new GenKeyQueryRunner using the given handler,
     * which will use the given column indexes for auto-generated keys retrieval
     *
     * @param ds             The <code>DataSource</code> to retrieve connections from.
     * @param keyHandler     The ResultSetHandler used to transform the generated keys into a Java Object
     * @param keyColsByIndex The indexes of the key columns which are used to auto-generated key retrieval.
     *                       May be empty if JDBC should decide on its own which columns suit best.
     */
    public GenKeyQueryRunner(DataSource ds, ResultSetHandler<Long> keyHandler, int... keyColsByIndex) {
        this(ds, keyColsByIndex, null, keyHandler);
    }

    /**
     * Construct a new GenKeyQueryRunner using the given handler,
     * which will use the given column indexes for auto-generated keys retrieval
     *
     * @param ds            The <code>DataSource</code> to retrieve connections from.
     * @param keyHandler    The ResultSetHandler used to transform the generated keys into a Java Object
     * @param keyColsByName The names of the key columns which are used to auto-generated key retrieval.
     *                      May be empty if JDBC should decide on its own which columns suit best.
     */
    public GenKeyQueryRunner(DataSource ds, ResultSetHandler<Long> keyHandler, String... keyColsByName) {
        this(ds, null, keyColsByName, keyHandler);
    }

    /**
     * Factory method that creates and initializes a
     * <code>PreparedStatement</code> object for the given SQL.
     * <code>QueryRunner</code> methods always call this method to prepare
     * statements for them.  Subclasses can override this method to provide
     * special PreparedStatement configuration if needed.  This implementation
     * calls the appropriate <code>conn.prepareStatement(sql, ...)</code>
     * method according to this GenKeyQueryRunner config.
     *
     * @param conn The <code>Connection</code> used to create the
     *             <code>PreparedStatement</code>
     * @param sql  The SQL statement to prepare.
     * @return An initialized <code>PreparedStatement</code>.
     * @throws java.sql.SQLException if a database access error occurs
     */
    @Override
    protected PreparedStatement prepareStatement(Connection conn, String sql) throws SQLException {
        PreparedStatement ps;

        if (keyColsByIndex != null && keyColsByIndex.length > 0) {
            ps = conn.prepareStatement(sql, keyColsByIndex);
        } else if (keyColsByName != null && keyColsByName.length > 0) {
            ps = conn.prepareStatement(sql, keyColsByName);
        } else {
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        }

        return ps;
    }

    // private static final Object INSERT_LOCK = new Object();

    /**
     * Write the new method , not override the super update method
     *
     * @param sql    The sql statement
     * @param params create the PreparedStatement
     * @return the number of row affected
     * @throws java.sql.SQLException SQL exception
     */
    public int insert(DBObject obj, Field keyField, String sql, Object... params) throws SQLException {
        Connection conn = this.prepareConnection();
        PreparedStatement stmt = null;
        int rows = 0;
        ResultSet autoKeyRs = null;
        // Clear generatedKeys first, in case an exception is thrown
        try {
            // lock this conn
            stmt = this.prepareStatement(conn, sql);
            this.fillStatement(stmt, params);
            rows = stmt.executeUpdate();
            autoKeyRs = stmt.getGeneratedKeys();
            if (rows == 1 && keyField != null) {
                Long generatedKeys = keyHandler.handle(autoKeyRs);
                if (keyField.getType().equals(Integer.TYPE)) {
                    keyField.set(obj, generatedKeys.intValue());
//                        System.out.println(sql);
                } else if (keyField.getType().equals(Long.TYPE)) {
                    keyField.set(obj, generatedKeys);
                }
            }
        } catch (SQLException e) {
            this.rethrow(e, sql, params);
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        } finally {
            close(autoKeyRs);
            close(stmt);
            close(conn);
        }
        return rows;
    }

    /**
     * Write the new method , not override the super update method
     *
     * @param conn  SQLConnection
     * @param  obj db object
     * @param sql    The sql statement
     * @param params create the PreparedStatement
     * @return the number of row affected
     * @throws java.sql.SQLException SQL exception
     */
    public int insert(Connection conn, DBObject obj, Field keyField, String sql, Object... params) throws SQLException {
        PreparedStatement stmt = null;
        int rows = 0;
        ResultSet autoKeyRs = null;
        // Clear generatedKeys first, in case an exception is thrown
        try {
            // lock this conn
            stmt = this.prepareStatement(conn, sql);
            this.fillStatement(stmt, params);
            rows = stmt.executeUpdate();
            autoKeyRs = stmt.getGeneratedKeys();
            if (rows == 1 && keyField != null) {
                Long generatedKeys = keyHandler.handle(autoKeyRs);
                if (keyField.getType().equals(Integer.TYPE)) {
                    keyField.set(obj, generatedKeys.intValue());
//                        System.out.println(sql);
                } else if (keyField.getType().equals(Long.TYPE)) {
                    keyField.set(obj, generatedKeys);
                }
            }
        } catch (SQLException e) {
            this.rethrow(e, sql, params);
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        } finally {
            close(autoKeyRs);
            close(stmt);
            close(conn);
        }
        return rows;
    }

    /**
     * Returns the generated keys, generated within the last update call
     *
     * @return the generated keys, may be <code>null</code>
     *         if no key was generated or if the update method was not invoked before.
     */
    // public T getGeneratedKeys() {
    //     return generatedKeys;
    // }
}
