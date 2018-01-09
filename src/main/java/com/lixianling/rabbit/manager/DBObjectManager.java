/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:03
 */
package com.lixianling.rabbit.manager;

import com.google.common.collect.ImmutableMap;
import com.lixianling.rabbit.IdGenerator;
import com.lixianling.rabbit.conf.DBObjectConfig;
import com.lixianling.rabbit.conf.DataSourceConfig;
import com.lixianling.rabbit.conf.RabbitConfig;
import com.lixianling.rabbit.conf.RedisConfig;
import com.lixianling.rabbit.dao.sql.SQLBuilder;
import org.apache.commons.dbutils.QueryRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * some db object generate information helper class
 * <p/>
 * there are some cache in the class;
 *
 * @author Xianling Li(hanklee)
 * $Id: DBObjectManager.java 40 2016-01-08 17:11:07Z hank $
 */
public final class DBObjectManager {
    private static final String TABLE_SUFFIX_ALL_NO_INCR = "_all_no_incr";
    private static final String TABLE_SUFFIX_NO_KEY = "_no_key";
    private static final String TABLE_SUFFIX_ALL = "_all";
    private static final String TABLE_SUFFIX_KEY = "_key";
    private static final String TABLE_SUFFIX_JSON_ATTR = "_json_attr";
    private static final String TABLE_SUFFIX_JSON_KEY = "_json_key";

    private static final String OBJECT_ATTR_SPLIT_KEY = ":";
    private static final String OBJECT_ATTR_EXCLUDE_SPLIT_KEY = ",";


    private static Map<String, Set<String>> ObjectColumnsCache = new ConcurrentHashMap<String, Set<String>>();

    private static Map<String, Class> ObjectTable = new ConcurrentHashMap<String, Class>();
    private static Map<Class, String> ObjectCache = new ConcurrentHashMap<Class, String>();
    private static Map<Class, Map<String, Field>> ObjectFieldCache = new ConcurrentHashMap<Class, Map<String, Field>>();

//    private static Map<String, String> TableCacheKeyFields = new ConcurrentHashMap<String, String>();


    private static Map<String, Field> TableInsertIncrKeyField = new ConcurrentHashMap<String, Field>();

//    private static Map<String, Field> TableUniqueField = new ConcurrentHashMap<String, Field>();

    public static IdGenerator idGenerator;

    private static MessageDigest md;
    private final static Object MD5LOCK = new Object();

    private DBObjectManager() {
    }

    static {
        init(RabbitManager.RABBIT_CONFIG);
    }

    protected static void register() {
        // nothing to do
    }

    /**
     * 注册数据库的一些配置属性
     */
    private static void init(RabbitConfig config) {
        try {
            md = MessageDigest.getInstance("MD5");
            idGenerator = new IdGenerator.DefaultIdGenerator();
            registerTables(config);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * register dbobject to relate database
     * @param config
     * @throws SQLException
     */
    private static void registerTables(RabbitConfig config) throws SQLException{
        for (DBObjectConfig.DBObjectSet dbset : config.dbObjectConfig.dbObjectSets) {

            if ("redis".equals(dbset.mode)) {
                registerRedisTables(config,dbset);
            } else if ("mysql".equals(dbset.mode)) {
                registerMySQLTables(config,dbset);
            } else if ("mix".equals(dbset.mode)) {
                registerRedisTables(config,dbset);
                registerMySQLTables(config,dbset);
            }

        }
    }

    private static void registerMySQLTables(RabbitConfig rabbitConfig, DBObjectConfig.DBObjectSet dbset) throws SQLException {
        // 获取数据源table相关的columns(类的属性与之相关)
        String key;
        Connection con = null;
        ResultSet rs = null;
        QueryRunner queryRunner = DataSourceManager.getQueryRunner(dbset.datasource);
        String table = dbset.table_name;
        try {
            con = queryRunner.getDataSource().getConnection();
            try {
                String incrKey = null;
                Set<String> keys = new HashSet<String>();
                Set<String> no_incr_key_columns = new HashSet<String>();
                Set<String> no_key_columns = new HashSet<String>();
                Set<String> all_columns = new HashSet<String>();

                rs = con.getMetaData().getPrimaryKeys(null, null, table);
                while (rs.next()) { // column name in the NO. 4
                    key = rs.getString(4);   //rs.getString("COLUMN_NAME");
                    keys.add(key);
                }
                rs.close();

                rs = con.getMetaData().getColumns(null, null, table, null);
                while (rs.next()) { // column name in the NO. 4
                    String name = rs.getString(4); //rs.getString("COLUMN_NAME");
                    all_columns.add(name);
                    if (keys.contains(name)) {
                        if (!"YES".equals(rs.getString("IS_AUTOINCREMENT"))) {
                            no_incr_key_columns.add(name);
                        } else {
                            incrKey = name;
                        }
//                    System.out.println(rs.getString("IS_AUTOINCREMENT")); "YES"
                        // nothing
                    } else {
                        no_incr_key_columns.add(name);
                        no_key_columns.add(name);
                    }
                }

                // 所有键值除了自增长的关键值
                ObjectColumnsCache.put(table + TABLE_SUFFIX_ALL_NO_INCR, no_incr_key_columns);
                // 所有键值除了关键值
                ObjectColumnsCache.put(table + TABLE_SUFFIX_NO_KEY, no_key_columns);
                // 所有键值
                ObjectColumnsCache.put(table + TABLE_SUFFIX_ALL, all_columns);
                // 所有关键值
                ObjectColumnsCache.put(table + TABLE_SUFFIX_KEY, keys);

                String className = dbset.class_name;

                Class clazz = Class.forName(className);

                // 缓存table类的属性
                registerTableClassField(clazz);

                Set<String> excludes = new HashSet<String>();
                String[] sss = dbset.exclude_field.split(OBJECT_ATTR_EXCLUDE_SPLIT_KEY);
                Collections.addAll(excludes, sss);

                no_key_columns.removeAll(excludes); // remove update attribute

                // must register json attribute and then json register key
                DBObjectManager.registerJSONAttr(table, all_columns.toArray(new String[all_columns.size()]));
                DBObjectManager.registerJSONKey(table, keys.toArray(new String[keys.size()]));
                DBObjectManager.registerTableClass(table, clazz);

                // 缓存table自增长的关键值
                if (incrKey != null) {
                    Field field = ObjectFieldCache.get(clazz).get(incrKey);
                    if (field != null) {
                        TableInsertIncrKeyField.put(table, field);
                    }
                }

                // 缓存table相关的类
                if ("true".equals(dbset.mark_table)) {
                    setTableNameByClass(clazz, table);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            if (rs != null)
                rs.close();
//            DbUtils.close(con);
        }

        // 缓存table相关的sql语句
        SQLBuilder.registerTable(table);


        // 缓存cache key
        // redis 缓存结构key:　$table_name_$cache_key_field_(upd|del|ins)_*
        // 默认system
//        TableCacheKeyFields.putAll(config.tableToCacheKeyField);
    }

    private static void registerRedisTables(RabbitConfig config, DBObjectConfig.DBObjectSet dbset) {
        String table = dbset.table_name;
        Set<String> keys = new HashSet<String>();
        Set<String> allField = new HashSet<String>();
        String[] sss = dbset.table_field.split(OBJECT_ATTR_SPLIT_KEY);
        Collections.addAll(allField, sss);

        String incrKey = dbset.incr_field;
        if (incrKey != null && incrKey.length() > 0) {
            sss = incrKey.split(OBJECT_ATTR_SPLIT_KEY);
            Collections.addAll(keys, sss);
        }

        String keyField = dbset.key_field;

        if (keyField != null && keyField.length() > 0) {
            sss = keyField.split(OBJECT_ATTR_SPLIT_KEY);
            Collections.addAll(keys, sss);
        }

        ObjectColumnsCache.put(table + TABLE_SUFFIX_KEY, keys);

        ObjectColumnsCache.put(table + TABLE_SUFFIX_ALL, allField);

        String className = dbset.class_name;
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 缓存table类的属性
        registerTableClassField(clazz);

        DBObjectManager.registerJSONAttr(table, allField.toArray(new String[allField.size()]));
        DBObjectManager.registerJSONKey(table, keys.toArray(new String[keys.size()]));
        DBObjectManager.registerTableClass(table, clazz);

        if (incrKey != null) {
            Field field = ObjectFieldCache.get(clazz).get(incrKey);
            if (field != null) {
                TableInsertIncrKeyField.put(table, field);
            }
        }
        // 缓存table相关的类
        setTableNameByClass(clazz, table);
    }

    private static void registerTableClassField(Class clazz) {
        if (ObjectFieldCache.get(clazz) == null) {
            ImmutableMap.Builder<String, Field> classMap = ImmutableMap.builder();
            Class uper = clazz;
            while (uper != null) {
                for (Field field : uper.getDeclaredFields()) {
                    if (Modifier.isFinal(field.getModifiers())
                            || Modifier.isStatic(field.getModifiers())
                            || !Modifier.isPublic(field.getModifiers())
                            || field.getType().isArray())
                        continue;
                    classMap.put(field.getName(), field);
                }
                uper = uper.getSuperclass();
            }
            ObjectFieldCache.put(clazz, classMap.build());
        }
    }

    protected static void registerTableClass(String table_name, Class clazz) {
        ObjectTable.put(table_name, clazz);
    }

    public static Class getClassByTable(String table_name) {
        return ObjectTable.get(table_name);
    }

    public static Field getInsertIncrKeyField(String table_name) {
        return TableInsertIncrKeyField.get(table_name);
    }

    /**
     * 注册 object 对应的 key 名字，生成 $table_name:$key1:$key2
     *
     * @param table_name
     * @param attrs
     */
    protected static void registerJSONKey(String table_name, String attrs) {
        registerJSONKey(table_name, attrs.split(OBJECT_ATTR_SPLIT_KEY));
    }

    /**
     * @param table_name
     * @param attrs      String...
     */
    protected static void registerJSONKey(String table_name, String... attrs) {
        Set<String> keyString = ObjectColumnsCache.get(table_name + TABLE_SUFFIX_JSON_KEY);
        if (keyString == null) {
            keyString = Collections.synchronizedSet(new HashSet<String>());
            ObjectColumnsCache.put(table_name + TABLE_SUFFIX_JSON_KEY, keyString);
        }
        Set<String> attrString = ObjectColumnsCache.get(table_name + TABLE_SUFFIX_JSON_ATTR);
        if (attrString == null) {
            attrString = Collections.synchronizedSet(new HashSet<String>());
            ObjectColumnsCache.put(table_name + TABLE_SUFFIX_JSON_ATTR, attrString);
        }
        Collections.addAll(keyString, attrs);
        Collections.addAll(attrString, attrs);
    }

    /**
     * 注册 object 对应的json 字符 生成 {$attr1:$value1,$attr2:value2.....}
     *
     * @param table_name
     * @param attrs      "dungeonId:times:reset:day:star:bide"
     */
    protected static void registerJSONAttr(String table_name, String attrs) {
        registerJSONAttr(table_name, attrs.split(OBJECT_ATTR_SPLIT_KEY));
    }

    protected static void registerJSONAttr(String table_name, String... attrs) {
        Set<String> attrString = ObjectColumnsCache.get(table_name + TABLE_SUFFIX_JSON_ATTR);
        if (attrString == null) {
            attrString = Collections.synchronizedSet(new HashSet<String>());
            ObjectColumnsCache.put(table_name + TABLE_SUFFIX_JSON_ATTR, attrString);
        }
        Collections.addAll(attrString, attrs);
//        ObjectColumnsCache.put(table_name + TABLE_SUFFIX_JSON_ATTR, Collections.synchronizedSet(attrString));
    }

    public static Set<String> getObjectJSONKeys(String table_name) {
        return ObjectColumnsCache.get(table_name + TABLE_SUFFIX_JSON_KEY);
    }

    public static Set<String> getObjectJSONAttr(String table_name) {
        return ObjectColumnsCache.get(table_name + TABLE_SUFFIX_JSON_ATTR);
    }

    public static Set<String> getTableAllColumns(String table) {
        return ObjectColumnsCache.get(table + TABLE_SUFFIX_ALL);
    }

    public static Set<String> getTableAllColumnsNoIncr(String table) {
        return ObjectColumnsCache.get(table + TABLE_SUFFIX_ALL_NO_INCR);
    }

    public static Set<String> getTableAllColumnsNoKey(String table) {
        return ObjectColumnsCache.get(table + TABLE_SUFFIX_NO_KEY);
    }

    public static Set<String> getTablePrimaryKey(String table) {
        return ObjectColumnsCache.get(table + TABLE_SUFFIX_KEY);
    }


    public static Map<String, Field> getClazzField(final Class clazz) {
        Map<String, Field> result = ObjectFieldCache.get(clazz);
        if (result == null) {
            registerTableClassField(clazz);
            result = ObjectFieldCache.get(clazz);
        }
        return result;
    }

    /**
     * generate table name by object
     *
     * @param clazz Clazz
     * @return table name
     */
    public static String getTableNameByObject(Class clazz) {
        return ObjectCache.get(clazz);
    }

    public static void setTableNameByClass(Class clazz, String table_name) {
        ObjectCache.put(clazz, table_name);
    }


    public static String md5(String input) {
        synchronized (MD5LOCK) {
            StringBuilder sb = new StringBuilder();
            try {
                byte[] bytesOfMessage = input.getBytes("UTF-8");

                byte[] thedigest = md.digest(bytesOfMessage);

                for (byte aThedigest : thedigest) {
                    sb.append(Integer.toHexString((aThedigest & 0xFF) | 0x100).substring(1, 3));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return sb.toString();
        }
    }

}
