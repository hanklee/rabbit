/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:24
 */
package com.lixianling.rabbit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.manager.DBObjectManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.json.JSONArray;
//import org.json.JSONObject;

/**
 * @author Xianling Li(hanklee)
 * $Id: DBObject.java 39 2016-01-08 12:04:37Z hank $
 */
public abstract class DBObject extends JSONObj {

    /**
     * Operation extends method
     */
    public void beforeInsert(DAO dao, String table, Object ignored) throws DBException {
        //nothing to do
    }

    public void beforeUpdate(DAO dao, String table, Object ignored) throws DBException {
        //nothing to do
    }

    public void beforeDelete(DAO dao, String table, Object ignored) throws DBException {
        //nothing to do
    }

    public void afterInsert(DAO dao, String table, Object ignored) throws DBException {
        //nothing to do
    }

    public void afterUpdate(DAO dao, String table, Object ignored) throws DBException {
        //nothing to do
    }

    public void afterDelete(DAO dao, String table, Object ignored) throws DBException {
        //nothing to do
    }


    /**
     * clone just copy the attribute value include, int, string, long, bool,
     * <p/>
     * the attribute value, which is array, exclude .
     *
     * @return
     */
    @Override
    public DBObject clone() {
        return (DBObject) super.clone();
    }

    @Override
    public String toString() {
        return __getValueToString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DBObject) {
            DBObject tmp = (DBObject) obj;
            if (tmp._keyString != null
                    && tmp._keyString.equals(this._keyString)) {
                return true;
            }
        }
        return false;
    }

//    /**
//     * 根据table 产生一个id
//     */
//    public void generateId(String table, IdGenerator idGen) {
//
//    }


    // key string format:"table_name:id1:id2:id3..."
    // table_name is heroes_bag ,only one id is 5;
    // heroes_bag:5
    private String _keyString = null;

    public String keyString(String source) throws DBException {
        if (_keyString == null) {
            _keyString = keyString(source, this.getTableName());
        }
        return _keyString;
    }

    public synchronized String keyString(String source, String table_name) throws DBException {
        return __getKeyStringByRegisterKey(source, table_name);
    }

    public Map<String, Object> ObjToMap(String table_name) {
        return ObjToMap(DBObjectManager.getObjectJSONAttr(table_name));
    }

    public Map<String, Object> ObjToMap(Set<String> attrs) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Field> allFields = getAllFields();
        try {
            for (String attr : attrs) {
                Field field = allFields.get(attr);
                Object value = field.get(this);
                if (value != null) {
                    if (value instanceof List) {
                        List tmp = (List) value;

                        if (tmp.size() > 0) {
                            Object tmpo = tmp.get(0);
                            if (tmpo instanceof DBObject) {
                                Map[] listObjs = new Map[tmp.size()];
                                int i = 0;
                                for (Object o : tmp) {
                                    listObjs[i] = ((DBObject) o).ObjToMap(attrs);
                                    i++;
                                }
                                result.put(attr, listObjs);
                            } else {
                                Object[] listObjs = new Object[tmp.size()];
                                int i = 0;
                                for (Object o : tmp) {
                                    listObjs[i] = o;
                                    i++;
                                }
                                result.put(attr, listObjs);
                            }

                        }

                    } else if (value instanceof DBObject) {
                        result.put(attr, ((DBObject) value).ObjToMap(attrs));
                    } else
                        result.put(attr, value);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * generate a string which  present table_name:key1:key2:...
     * <p/>
     * key1 is number one key value of the table
     *
     * @param table_name String
     * @return String
     */
    private String __getKeyStringByRegisterKey(String source, String table_name) throws DBException {
        Set<String> keys = DBObjectManager.getObjectJSONKeys(source, table_name);
        Map<String, Field> allFields = getAllFields();
        StringBuilder sb = new StringBuilder();
        sb.append(table_name);
        if (keys != null && keys.size() >= 1) {
            try {
                for (String key : keys) {
                    Field field = allFields.get(key);
                    Object value = null;
                    if (field != null) {
                        value = field.get(this);
                    }

                    if (value != null) {
                        if (value instanceof Integer) {
                            if (0 == ((Integer) value)) {
                                throw new DBException("No key value.");
                            }
                        }
                        sb.append(":").append(value);
                    } else {
                        throw new DBException("No key value.");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new DBException(ex.getMessage());
            }
        } else {
            // no Primary Keys
            sb.append(":").append(DBObjectManager.idGenerator.generateLongId());
        }
        return sb.toString();
    }


    /**
     * print all object attribute and its value
     * <p/>
     * format:  attr1,value1;attr2,value2;....
     *
     * @return string
     */
    private String __getValueToString() {
        StringBuilder sb = new StringBuilder();
        try {
            Map<String, Field> allFields = getAllFields();
            for (Field field : allFields.values()) {
                Class type = field.getType();
                if (Boolean.TYPE == type || Integer.TYPE == type
                        || Long.TYPE == type || Double.TYPE == type
                        || type.equals(String.class)) {
                    String name = field.getName();
                    Object o = "";
                    try {
                        o = field.get(this);
                        if (o == null)
                            o = "";
                    } catch (Exception ignored) {
                    }
                    sb.append(name).append(",").append(o).append(";");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }

    public Object getValueByField(String name) {
        Field field = getAllFields().get(name);
        if (field != null) {
            try {
                return field.get(this);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * get  SQL Server relate this class table name
     *
     * @return table name
     */

    public String getTableName() {
        Object oname = getValueByField("table_name");
        if (oname == null) {
            Class clazz = getClass();
            String tname = DBObjectManager.getTableNameByObject(clazz);
            if (tname == null) {
                tname = clazz.getSimpleName().toLowerCase() + "s";
                DBObjectManager.setTableNameByClass(clazz, tname);
            }
            return tname;
        } else {
            return oname.toString();
        }
    }

    public JSONObject toDBJson() {
        JSONObject json = new JSONObject();
        String source = DBObjectManager.getDefaultSource();
        setJsonValueByDBAttr(json, source, getTableName());
        return json;
    }

    public JSONObject toDBJson(String source) {
        JSONObject json = new JSONObject();
        setJsonValueByDBAttr(json, source, getTableName());
        return json;
    }

    public JSONObject toDBJson(String source, String table_name) {
        JSONObject json = new JSONObject();
        setJsonValueByDBAttr(json, source, table_name);
        return json;
    }

    protected void setJsonValueByDBAttr(final JSONObject json, final String source, final String table_name) {
        Set<String> attrs = DBObjectManager.getObjectJSONAttr(source, table_name);
        Map<String, Field> allFields = getAllFields();
        try {
            for (String attr : attrs) {
                Field field = allFields.get(attr);
                Object value = null;
                if (field != null) {
                    value = field.get(this);
                }

                if (value != null) {
                    if (value instanceof List) {
                        List tmp = (List) value;
                        JSONArray jsonArray = new JSONArray();
                        if (tmp.size() > 0) {
                            Object check = tmp.get(0);
                            if (check instanceof DBObject) {
                                String tmp_table_name = table_name + "_" + attr;
                                for (Object o : tmp) {
//                                    jsonArray.put(((DBObject) o).toDBJson(tmp_table_name));
                                    jsonArray.add(((DBObject) o).toDBJson(tmp_table_name));
                                }
                                json.put(attr, jsonArray);
                            } else {
                                for (Object o : tmp) {
//                                    jsonArray.put(o);
                                    jsonArray.add(o);
                                }

                            }
                        }
                        json.put(attr, jsonArray);
                    } else if (value instanceof DBObject) {
                        json.put(attr, ((DBObject) value).toDBJson(source));
                    } else
                        json.put(attr, value);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


//    /*
//
//                 REDIS KEY method
//
//     */
//    public String updateKEY(final String table) throws DBException {
//        return CacheManager.CACHE_SUFFIX + this.getCacheKeyFieldValue(table)
//                + CacheManager.UPDATE_CACHE_OP + this.toKeyString(table);
//    }
//
//    public String deleteKEY(final String table) throws DBException {
//        return CacheManager.CACHE_SUFFIX + this.getCacheKeyFieldValue(table)
//                + CacheManager.DELETE_CACHE_OP + this.toKeyString(table);
//    }
//
//    public String insertKEY(final String table) throws DBException {
//        return CacheManager.CACHE_SUFFIX + this.getCacheKeyFieldValue(table)
//                + CacheManager.INSERT_CACHE_OP + this.toKeyString(table);
//    }


//    /**
//     * reference : http://redis.io/topics/twitter-clone
//     * <p/>
//     * 1: get key id : next_$table_name_id
//     * <p/>
//     * 2: hmset $table_name:$key_id ....
//     * <p/>
//     * 3: if object has unique value , set $table_name, $unique_value, $key_id
//     * <p>
//     * for index
//     * <p>
//     * if you want redis set $table_name, $unique_value, $key_id,
//     * overwrite this method
//     *
//     * @return string unique value
//     */
//    public String uniqueValue() {
//        Field field = DBObjectManager.getUniqueField(getTableName());
//        if (field != null) {
//            try {
//                Object value = field.get(this);
//                return String.valueOf(value);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }


    /**
     * DBObject builder
     */
    public static class DBObjectBuilder {

        private Class clazz;
        private Map<String, Object> data;


        public DBObjectBuilder(Class clazz) {
            this.clazz = clazz;
            this.data = new HashMap<String, Object>();
        }

        public DBObjectBuilder addData(String fname, Object fvalue) {
            this.data.put(fname, fvalue);
            return this;
        }


        public DBObject build() throws DBException {
            try {
                DBObject obj = (DBObject) clazz.newInstance();
                obj.MapToObj(this.data);
                return obj;
            } catch (Exception e) {
                throw new DBException(e.getMessage());
            }
        }
    }
}
