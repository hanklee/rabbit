/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-07 01:09
 */
package com.lixianling.rabbit.dao.sql;

import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.JSONObj;

import java.util.Map;

/**
 * @author Xianling Li(hanklee)
 * $Id: MapToDBObject.java 36 2016-01-06 17:24:04Z hank $
 */
public class MapToDBObject<T extends DBObject> implements MapToObject<T> {

    private Class<T> clazz;
    private MapToObjectHandler<T> handler;

    public MapToDBObject(Class<T> clazz) {
        this.clazz = clazz;
        this.handler = new BasicHandler<T>();
    }

    public MapToDBObject(Class<T> clazz, MapToObjectHandler<T> handler) {
        this.clazz = clazz;
        this.handler = handler;
    }

    @Override
    public T toObject(Map<String, Object> map) {
        T obj = null;
        try {
            obj = JSONObj.newDataObj(clazz, map);
            if (!this.handler.handler(obj)) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static <T extends DBObject> MapToObject<T> newHandler(final Class<T> clazz) {
        return new MapToDBObject<T>(clazz);
    }

    public static <T extends DBObject> MapToDBObjectHandler<T> newRsHandler(final Class<T> clazz) {
        return new MapToDBObjectHandler<T>(clazz);
    }

    public static <T extends DBObject> MapToDBObjectListHandler<T> newListHandler(final Class<T> clazz) {
        return new MapToDBObjectListHandler<T>(clazz);
    }

    public static <T extends DBObject> MapToDBObjectListHandler<T> newListHandler(final Class<T> clazz
            , MapToObjectHandler<T> handler) {
        return new MapToDBObjectListHandler<T>(clazz, handler);
    }

    class BasicHandler<E> implements MapToObjectHandler<E> {
        @Override
        public boolean handler(E o) {
            return true;
        }
    }
}
