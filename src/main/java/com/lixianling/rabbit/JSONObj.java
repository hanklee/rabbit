/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 20:19
 */
package com.lixianling.rabbit;

import com.google.common.primitives.Primitives;
import com.lixianling.rabbit.manager.DBObjectManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: JSONObj.java 39 2016-01-08 12:04:37Z hank $
 */
public abstract class JSONObj implements Serializable {
    private Map<String, Field> _allFields = null;

    public Map<String, Field> getAllFields() {
        if (_allFields == null) {
            _allFields = DBObjectManager.getClazzField(this.getClass());
        }
        return _allFields;
    }

    public void MapToObj(Map<String, Object> map) {
        Map<String, Field> allFields = getAllFields();
        for (String fieldName : map.keySet()) {
            Field field = allFields.get(fieldName);
            Object value = map.get(fieldName);
            if (field != null) {
                Class type = field.getType();
                __setValueToObj(value, field, type);
            }
        }
    }

    /**
     * JSON OPERATION
     */

    public void JsonToObj(JSONObject json) {
        Map<String, Field> allFields = getAllFields();
        for (Object attr : json.keySet()) {
            Object value = json.opt((String) attr);
            Field field = allFields.get(attr);
            if (field != null
                    && value != null) {
                Class type = field.getType();
                __setValueToObj(value, field, type);
            }
        }
    }

    public void JsonToObj(JSONObject json,String table_name) {
        Set<String> attrs = DBObjectManager.getObjectJSONAttr(table_name);
        Map<String, Field> allFields = getAllFields();
        for (Object attr : attrs) {
            Object value = json.opt((String) attr);
            Field field = allFields.get(attr);
            if (field != null
                    && value != null) {
                Class type = field.getType();
                __setValueToObj(value, field, type);
            }
        }
    }

    private void __setValueToObj(final Object value, final Field field, final Class type) {
        try {
            if (Boolean.TYPE == type) {
                if (value instanceof Boolean) {
                    field.setBoolean(this, (Boolean) value);
                } else {
                    int ivalue = value instanceof Number ? ((Number) value).intValue()
                            : Integer.parseInt((String) value);
                    field.setBoolean(this, ivalue == 1);
                }
            } else if (type.isPrimitive() || type.equals(String.class)
                    || Primitives.isWrapperType(type)) {
                field.set(this, value);
            } else if (List.class.isAssignableFrom(type)) {
                if (value instanceof JSONArray) {
                    ParameterizedType listType = (ParameterizedType) field.getGenericType();
                    Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                    if (JSONObj.class.isAssignableFrom(listClass)) {
                        List tmp = new ArrayList();
                        JSONArray jsonArray = (JSONArray) value;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Object o = listClass.newInstance();
                            ((JSONObj) o).JsonToObj(jsonArray.getJSONObject(i));
                            tmp.add(o);
                        }
                        field.set(this, tmp);
                    } else {
                        List tmp = new ArrayList();
                        JSONArray jsonArray = (JSONArray) value;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            tmp.add(jsonArray.get(i));
                        }
                        field.set(this, tmp);
                    }
                } else if (value instanceof List) {
                    ParameterizedType listType = (ParameterizedType) field.getGenericType();
                    Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                    List tmp = new ArrayList();
                    if (JSONObj.class.isAssignableFrom(listClass)) {
                        List mapArray = (List) value;
                        for (Object mapData : mapArray) {
                            Object o = listClass.newInstance();
                            ((JSONObj) o).MapToObj((Map) mapData);
                            tmp.add(o);
                        }
                    } else {
                        List mapArray = (List) value;
                        for (Object mapData : mapArray) {
                            tmp.add(mapData);
                        }
                    }
                    field.set(this, tmp);
                } else if (value instanceof Map[]) {
                    ParameterizedType listType = (ParameterizedType) field.getGenericType();
                    Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                    List tmp = new ArrayList();
                    if (JSONObj.class.isAssignableFrom(listClass)) {
                        Map[] mapArray = (Map[]) value;
                        for (int i = 0; i < mapArray.length; i++) {
                            Object o = listClass.newInstance();
                            ((JSONObj) o).MapToObj(mapArray[i]);
                            tmp.add(o);
                        }
                    }
                    field.set(this, tmp);
                }
                //  for mongodb
                //
//                    else if (value instanceof BasicDBObject) {
//                        Object o = type.newInstance();
//                        ((DBObject) o).MapToObj((BasicDBObject) value);
//                        field.set(obj, o);
//                    }

            } else if (JSONObj.class.isAssignableFrom(type)) {
                if (value instanceof JSONObject) {
                    Object o = type.newInstance();
                    ((JSONObj) o).JsonToObj((JSONObject) value);
                    field.set(this, o);
                } else if (value instanceof String) {
                    Object o = type.newInstance();
                    ((JSONObj) o).JsonToObj(new JSONObject((String) value));
                    field.set(this, o);
                } else if (value instanceof Map) {
                    Object o = type.newInstance();
                    ((JSONObj) o).MapToObj((Map)value);
                    field.set(this, o);
                }
            }
//            else {
//                field.set(this, value);
//            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        Map<String, Field> allFields = getAllFields();
        try {
            for (String attr : allFields.keySet()) {
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
                            if (check instanceof JSONObj) {
                                for (Object o : tmp) {
                                    jsonArray.put(((JSONObj) o).toJson());
                                }
                            } else {
                                for (Object o : tmp) {
                                    jsonArray.put(o);
                                }
                            }
                        }
                        json.put(attr, jsonArray);
                    } else if (value instanceof JSONObj) {
                        json.put(attr, ((JSONObj) value).toJson());
                    } else
                        json.put(attr, value);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return json;
    }



    /*

                 O B J E C T       M E T H O D

     */

    /**
     * clone just copy the attribute value include, int, string, long, bool,
     * <p/>
     * the attribute value, which is array, exclude .
     *
     * @return
     */
    @Override
    public JSONObj clone() {
        Object obj = null;
        try {
            Class myClazz = getClass();
            obj = myClazz.newInstance();
            __getObjectValue(obj, this);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (JSONObj) obj;
    }

    /**
     * Copy the attribute value of same class object.
     *
     * @param object JSONObj
     */
    public void copy(JSONObj object) {
        __getObjectValue(this, object);
    }

    public void merge(JSONObj object) {
        __mergeObjectValue(this, object);
    }

    private void __getObjectValue(final Object setObj, final Object getObj) {
        try {
            Map<String, Field> allFields = getAllFields();
            if (!setObj.getClass().isAssignableFrom(getObj.getClass()))
                allFields = DBObjectManager.getClazzField(getObj.getClass());
            for (Field field : allFields.values()) {
                Object value = field.get(getObj);
                if (value != null) {
                    field.set(setObj, value);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * only support copy the integer double, float, String
     *
     * @param setObj set object value
     * @param getObj get object value
     */
    private void __mergeObjectValue(final Object setObj, final Object getObj) {
        try {
            Map<String, Field> allFields = getAllFields();
            if (!setObj.getClass().isAssignableFrom(getObj.getClass()))
                allFields = DBObjectManager.getClazzField(getObj.getClass());
            for (Field field : allFields.values()) {
                Object value = field.get(getObj);
                if (value != null) {
                    Class type = field.getType();

                    if (Integer.TYPE == type
                            || Double.TYPE == type
                            || Float.TYPE == type
                            || Short.TYPE == type
                            || Long.TYPE == type) {
                        Number number = (Number) value;
                        if (number.intValue() != 0)
                            field.set(setObj, value);
                    } else if (value instanceof String) {
                        field.set(setObj, value);
                    } else if (Boolean.TYPE == type) {
                        if ((Boolean) value)
                            field.set(setObj, true);
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
