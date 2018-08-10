/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:03
 */
package com.lixianling.rabbit.manager;

import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.IdGenerator;
import com.lixianling.rabbit.conf.DBObjectConfig;
import com.lixianling.rabbit.conf.RabbitConfig;
import com.lixianling.rabbit.conf.TableConfig;
import com.lixianling.rabbit.dao.sql.SQLBuilder;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
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


    private static Map<String, Set<String>> ObjectColumnsCache = new ConcurrentHashMap<String, Set<String>>(40, 0.5f);

    private static Map<String, Class> ObjectTable = new ConcurrentHashMap<String, Class>(40, 0.5f);
    private static Map<Class, String> ObjectCache = new ConcurrentHashMap<Class, String>(40, 0.5f);
//    private static Map<Class, String> ObjectSource = new ConcurrentHashMap<Class, String>(40, 0.5f);
    private static Map<Class, Map<String, Field>> ObjectFieldCache = new ConcurrentHashMap<Class, Map<String, Field>>(40, 0.5f);

    private static Map<String, String> TableInsertIncrKeyField = new ConcurrentHashMap<String, String>(40, 0.5f);

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
     *
     * @param config config data
     * @throws DBException db Exception
     */
    private static void registerTables(RabbitConfig config) throws DBException {
        for (TableConfig.TableObject tableObject : config.jsonTableConfig.jsontables) {
            registerJsonTables(tableObject);
        }

        for (DBObjectConfig.DBObjectSet dbset : config.dbObjectConfig.dbObjectSets) {
            registerObject(dbset);
        }
    }

    protected static void registerMySQLTable(String datasource, String table) throws DBException {
        if (getTableAllColumns(table) != null) {
            return;
        }
        String key;
        Connection con = null;
        ResultSet rs = null;
        QueryRunner queryRunner = DataSourceManager.getQueryRunner(datasource);
        try {
            try {
                con = queryRunner.getDataSource().getConnection();
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

                ImmutableSet.Builder<String> keysBuilder = ImmutableSet.builder();
                // 所有关键值
                ObjectColumnsCache.put(table + TABLE_SUFFIX_KEY, keysBuilder.addAll(keys).build());

                rs = con.getMetaData().getColumns(null, null, table, null);
                while (rs.next()) { // column name in the NO. 4
                    String name = rs.getString(4); //rs.getString("COLUMN_NAME");
                    all_columns.add(name);
                    if (getTablePrimaryKey(table).contains(name)) {
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

                ImmutableSet.Builder<String> no_incr_key_columnsBuilder = ImmutableSet.builder();
                ImmutableSet.Builder<String> no_key_columnsBuilder = ImmutableSet.builder();
                ImmutableSet.Builder<String> all_columnsBuilder = ImmutableSet.builder();

                // 所有键值除了自增长的关键值
                ObjectColumnsCache.put(table + TABLE_SUFFIX_ALL_NO_INCR, no_incr_key_columnsBuilder.addAll(no_incr_key_columns).build());
                // 所有键值除了关键值
                ObjectColumnsCache.put(table + TABLE_SUFFIX_NO_KEY, no_key_columnsBuilder.addAll(no_key_columns).build());
                // 所有键值
                ObjectColumnsCache.put(table + TABLE_SUFFIX_ALL, all_columnsBuilder.addAll(all_columns).build());

                // 缓存table自增长的关键值
                if (incrKey != null) {
                    TableInsertIncrKeyField.put(table, incrKey);
                }

                // must register json attribute and then json register key
                DBObjectManager.registerJSONAttr(table, all_columns.toArray(new String[all_columns.size()]));
                DBObjectManager.registerJSONKey(table, keys.toArray(new String[keys.size()]));
                // 缓存table相关的sql语句
                SQLBuilder.registerTable(table);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new DBException(ex.getMessage());
            }
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (con != null) {
                    DbUtils.close(con);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void registerObject(DBObjectConfig.DBObjectSet dbset) throws DBException {
        // 获取数据源table相关的columns(类的属性与之相关)
        String table = dbset.table_name;
        try {
            String className = dbset.class_name;
            Class clazz = Class.forName(className);
            // table对应的类
            registerTableClassField(clazz);
            if ("true".equals(dbset.mark_class)) {
                DBObjectManager.registerTableClass(table, clazz);
            }

            // 缓存table相关的类
            if ("true".equals(dbset.mark_table)) {
                setTableNameByClass(clazz, table);
            }

//            if (dbset.mode.contains("elastic")
//                    || dbset.mode.contains("mongo")) {
//                ObjectSource.put(clazz, dbset.datasource);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void registerJsonTables(TableConfig.TableObject tableObject) throws DBException {
        String table = tableObject.table_name;
        if (getTableAllColumns(table) != null) {
            return;
        }
        Set<String> keys = new HashSet<String>();
        Set<String> allField = new HashSet<String>();
        String[] sss = tableObject.table_field.split(OBJECT_ATTR_SPLIT_KEY);
        Collections.addAll(allField, sss);

        String incrKey = tableObject.incr_field;
        if (incrKey != null && incrKey.length() > 0) {
//            sss = incrKey.split(OBJECT_ATTR_SPLIT_KEY);
//            Collections.addAll(keys, sss);
            keys.add(incrKey);
        }

        String keyField = tableObject.key_field;

        if (keyField != null && keyField.length() > 0) {
            sss = keyField.split(OBJECT_ATTR_SPLIT_KEY);
            Collections.addAll(keys, sss);
        }

        ImmutableSet.Builder<String> keysBuilder = ImmutableSet.builder();
        ImmutableSet.Builder<String> allFieldBuilder = ImmutableSet.builder();
        ObjectColumnsCache.put(table + TABLE_SUFFIX_KEY, keysBuilder.addAll(keys).build());
        ObjectColumnsCache.put(table + TABLE_SUFFIX_ALL, allFieldBuilder.addAll(allField).build());

        DBObjectManager.registerJSONAttr(table, allField.toArray(new String[allField.size()]));
        DBObjectManager.registerJSONKey(table, keys.toArray(new String[keys.size()]));

        if (incrKey != null) {
            TableInsertIncrKeyField.put(table, incrKey);
        }
    }

    private static void registerTableClassField(Class clazz) {
        if (ObjectFieldCache.get(clazz) == null) {
            ImmutableMap.Builder<String, Field> classMap = ImmutableMap.builder();
//            Class uper = clazz;
//            while (uper != null) {
            List<FieldInfo> fieldInfoList = TypeUtils.computeGetters(clazz, null);
            for (FieldInfo info : fieldInfoList) {
                if (info.field != null) {
                    if (Modifier.isFinal(info.field.getModifiers())
                            || Modifier.isStatic(info.field.getModifiers())
                            || info.field.getType().isArray()) {
                        continue;
                    }
                    if ((Modifier.isPublic(info.field.getModifiers()))) {
                        classMap.put(info.field.getName(), info.field);
                    } else if (Modifier.isPublic(info.method.getModifiers()) && !info.name.equals("allFields")) {
                        classMap.put(info.field.getName(), info.field);
                    }
                }
            }
//                for (Field field : uper.getDeclaredFields()) {
//                    if (Modifier.isFinal(field.getModifiers())
//                            || Modifier.isStatic(field.getModifiers())
//                            || !Modifier.isPublic(field.getModifiers())
//                            || field.getType().isArray())
//                        continue;
//                    classMap.put(field.getName(), field);
//                }
//                uper = uper.getSuperclass();
//            }
            ObjectFieldCache.put(clazz, classMap.build());
        }
    }

    protected static void registerTableClass(String table_name, Class clazz) {
        ObjectTable.put(table_name, clazz);
    }

    public static <T extends DBObject> Class<T> getClassByTable(String table_name) {
        return ObjectTable.get(table_name);
    }

    public static String getInsertIncrKeyField(String table_name) {
        return TableInsertIncrKeyField.get(table_name);
    }

    public static Field getInsertIncrKeyField(String table_name, DBObject obj) {
        String keyFieldName = TableInsertIncrKeyField.get(table_name);
        Field keyField = null;
        if (keyFieldName != null) {
            keyField = obj.getAllFields().get(keyFieldName);
        }
        return keyField;
    }

    /**
     * 注册 object 对应的 key 名字，生成 $table_name:$key1:$key2
     *
     * @param table_name 表名
     * @param attrs      属性
     */
    protected static void registerJSONKey(String table_name, String attrs) {
        registerJSONKey(table_name, attrs.split(OBJECT_ATTR_SPLIT_KEY));
    }

    /**
     * @param table_name 表名
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
     * @param table_name 表名
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
