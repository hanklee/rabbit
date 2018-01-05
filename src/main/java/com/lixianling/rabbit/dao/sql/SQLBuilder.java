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
 *         $Id: SQLBuilder.java 39 2016-01-08 12:04:37Z hank $
 */
public class SQLBuilder {

    private static Map<String, String> SQLINSERTCACHE = new ConcurrentHashMap<String, String>();
    private static Map<String, String> SQLUPDATECACHE = new ConcurrentHashMap<String, String>();
    private static Map<String, String> SQLDELETECACHE = new ConcurrentHashMap<String, String>();

    public static void registerTable(final String table){
        SQLINSERTCACHE.put(table, makeInsertSQL(table));
        SQLUPDATECACHE.put(table, makeUpdateSQL(table));
        SQLDELETECACHE.put(table, makeDeleteSQL(table));
    }

        /*

    SQL CACHE BUILDER

     */

    public static String getInsertSQLByTable(final String table) {
        return SQLINSERTCACHE.get(table);
    }

    public static String getUpdateSQLByTable(final String table) {
        return SQLUPDATECACHE.get(table);
    }

    public static String getDeleteSQLByTable(final String table) {
        return SQLDELETECACHE.get(table);
    }

    private static String makeInsertSQL(String table) {
        StringBuilder s = new StringBuilder("insert into ").append(table).append("(");
        StringBuilder sv = new StringBuilder(" values(");

        Set<String> columns = DBObjectManager.getTableAllColumnsNoIncr(table); // true means if it is not auto increase then add key's column
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

    private static String makeUpdateSQL(String table) {
        Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(table);
        StringBuilder s = new StringBuilder("update ").append(table).append(" set ");
        Set<String> columns = DBObjectManager.getTableAllColumnsNoKey(table);
        int size = columns.size();
        for (String column : columns) {
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

    private static String makeDeleteSQL(String table) {
        Set<String> primary_keys = DBObjectManager.getTablePrimaryKey(table);

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
