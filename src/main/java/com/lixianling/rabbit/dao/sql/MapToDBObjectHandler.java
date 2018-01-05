/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-07 01:10
 */
package com.lixianling.rabbit.dao.sql;

import com.lixianling.rabbit.DBObject;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: MapToDBObjectHandler.java 36 2016-01-06 17:24:04Z hank $
 */
public class MapToDBObjectHandler<T extends DBObject> implements ResultSetHandler<T> {

    private RowProcessor convert;
    private MapToDBObject<T> mapToObject;

    public MapToDBObjectHandler(Class<T> clazz) {
        mapToObject = new MapToDBObject<T>(clazz);
        convert = new BasicRowProcessor();
    }

    @Override
    public T handle(ResultSet rs) throws SQLException {
        Map<String,Object> map = rs.next() ? this.convert.toMap(rs) : null;
        if (map == null) return null;
        return mapToObject.toObject(map);
    }
}
