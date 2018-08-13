/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-07 01:26
 */
package com.lixianling.rabbit.dao.sql;

import com.lixianling.rabbit.manager.DBObjectManager;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Xianling Li(hanklee)
 * $Id: SQLBuilder.java 39 2016-01-08 12:04:37Z hank $
 */
public class SQLBuilder {

    private static Map<String, String> SQLCACHE = new ConcurrentHashMap<String, String>(80, 0.5f);

    private static final String OP_INSERT = "_OP_INSERT";
    private static final String OP_UPDATE = "_OP_UPDATE";
    private static final String OP_DELETE = "_OP_DELETE";
    private static final String OP_GETOBJ = "_OP_GETOBJ";

    public static void registerTable(String source, final String table) {
        SQLCACHE.put(source + ":" + table + OP_INSERT, makeInsertSQL(source, table));
        SQLCACHE.put(source + ":" + table + OP_UPDATE, makeUpdateSQL(source, table, null));
        SQLCACHE.put(source + ":" + table + OP_DELETE, makeDeleteSQL(source, table));
        SQLCACHE.put(source + ":" + table + OP_GETOBJ, makeGetObjectSQL(source, table));
    }

    public static String getOpUpdate(String source, final String table, Set<String> excludes) {
        if (excludes == null) {
            return SQLCACHE.get(source + ":" + table + OP_UPDATE);
        }
        return makeUpdateSQL(source, table, excludes);
    }

    public static String makeUpdateWithFieldsSQL(String source, final String table, String[] fields) {
        Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(source, table);
        StringBuilder s = new StringBuilder("update ").append(table).append(" set ");
        int size = fields.length;
        for (String column : fields) {
            if (--size == 0) {
                // last item
                s.append('`').append(column).append("`=? ");
            } else {
                s.append('`').append(column).append("`=?, ");
            }
        }
        s.append(" where ");
        int key_size = primary_keys.size();
        int kye_i = 1;
        for (String primary_key : primary_keys) {
            s.append('`').append(primary_key).append("` = ? ");
            if (kye_i != key_size)
                s.append(" AND ");
            kye_i++;
        }
        return s.toString();
    }

    public static String makeUpdateWithAllFieldsSQL(final String table, String[] fields, String[] whereFields) {
        StringBuilder s = new StringBuilder("update ").append(table).append(" set ");
        int size = fields.length;
        for (String column : fields) {
            if (--size == 0) {
                // last item
                s.append('`').append(column).append("`=? ");
            } else {
                s.append('`').append(column).append("`=?, ");
            }
        }
        s.append(" where ");
        int key_size = whereFields.length;
        int kye_i = 1;
        for (String primary_key : whereFields) {
            s.append('`').append(primary_key).append("` = ? ");
            if (kye_i != key_size)
                s.append(" AND ");
            kye_i++;
        }
        return s.toString();
    }

    /*

    SQL CACHE BUILDER

     */

    public static String getInsertSQLByTable(String source, final String table) {
        return SQLCACHE.get(source + ":" + table + OP_INSERT);
    }

    public static String getUpdateSQLByTable(String source, final String table) {
        return SQLCACHE.get(source + ":" + table + OP_UPDATE);
    }

    public static String getDeleteSQLByTable(String source, final String table) {
        return SQLCACHE.get(source + ":" + table + OP_DELETE);
    }

    public static String getObjectSQLByTable(String source, final String table) {
        return SQLCACHE.get(source + ":" + table + OP_GETOBJ);
    }

    public static String makeGetObjectsSQL(String table, String[] fields) {
        StringBuilder s = new StringBuilder("SELECT * FROM ").append(table);
        s.append(" WHERE ");
        int key_size = fields.length;
        int kye_i = 1;
        for (String primary_key : fields) {
            s.append('`').append(primary_key).append("` = ? ");
            if (kye_i != key_size)
                s.append(" AND ");
            kye_i++;
        }
        return s.toString();
    }

    public static String makeDeleteObjectsSQL(String table, String[] fields) {
        StringBuilder s = new StringBuilder("DELETE FROM ");
        s.append(table).append(" WHERE ");

        int key_size = fields.length;
        int kye_i = 1;
        for (String primary_key : fields) {
            s.append('`').append(primary_key).append("` = ? ");
            if (kye_i != key_size)
                s.append(" AND ");
            kye_i++;
        }
        return s.toString();
    }

    /**
     * PRIVATE METHODS
     */

    private static String makeGetObjectSQL(String source, String table) {
        Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(source, table);
        StringBuilder s = new StringBuilder("SELECT * FROM ").append(table);
        s.append(" WHERE ");
        int key_size = primary_keys.size();
        int kye_i = 1;
        for (String primary_key : primary_keys) {
            s.append('`').append(primary_key).append("` = ? ");
            if (kye_i != key_size)
                s.append(" AND ");
            kye_i++;
        }
        return s.toString();
    }

    private static String makeInsertSQL(String source, String table) {
        StringBuilder s = new StringBuilder("insert into ").append(table).append("(");
        StringBuilder sv = new StringBuilder(" values(");

        Set<String> columns = DBObjectManager.getTableAllColumnsNoIncr(source, table); // true means if it is not auto increase then add key's column
        int size = columns.size();
        for (String column : columns) {
            if (--size == 0) {
                // last item
                s.append('`').append(column).append("`)");
                sv.append("?)");
            } else {
                s.append('`').append(column).append("`,");
                sv.append("?,");
            }
        }
        return s.append(sv).toString();
    }

    private static String makeUpdateSQL(String source, String table, Set<String> excludes) {
        Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(source, table);
        StringBuilder s = new StringBuilder("update ").append(table).append(" set ");
        Set<String> columns = DBObjectManager.getTableAllColumnsNoKey(source, table);
        int rsize = (excludes == null) ? 0 : excludes.size();
        int size = columns.size() - rsize;
        for (String column : columns) {
            if (excludes == null || !excludes.contains(column)) {
                if (--size == 0) {
                    // last item
                    s.append('`').append(column).append("`=? ");
                } else {
                    s.append('`').append(column).append("`=?, ");
                }
            }
        }
        s.append(" where ");
        int key_size = primary_keys.size();
        int kye_i = 1;
        for (String primary_key : primary_keys) {
            s.append('`').append(primary_key).append("` = ? ");
            if (kye_i != key_size)
                s.append(" AND ");
            kye_i++;
        }
        return s.toString();
    }

    private static String makeDeleteSQL(String source, String table) {
        Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(source, table);

        StringBuilder s = new StringBuilder("delete from ");
        s.append(table).append(" where ");

        int key_size = primary_keys.size();
        int kye_i = 1;
        for (String primary_key : primary_keys) {
            s.append('`').append(primary_key).append("` = ? ");
            if (kye_i != key_size)
                s.append(" AND ");
            kye_i++;
        }
        return s.toString();
    }

}
