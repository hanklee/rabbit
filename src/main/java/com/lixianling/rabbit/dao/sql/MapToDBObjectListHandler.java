/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-07 01:11
 */
package com.lixianling.rabbit.dao.sql;

import com.lixianling.rabbit.DBObject;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Xianling Li(hanklee)
 *         $Id: MapToDBObjectListHandler.java 36 2016-01-06 17:24:04Z hank $
 */
public class MapToDBObjectListHandler<T extends DBObject> implements ResultSetHandler<List<T>> {

    private RowProcessor convert;
    private MapToDBObject<T> mapToObject;

    public MapToDBObjectListHandler(Class<T> clazz) {
        mapToObject = new MapToDBObject<T>(clazz);
        convert = new BasicRowProcessor();
    }

    public MapToDBObjectListHandler(Class<T> clazz, MapToObjectHandler<T> handler) {
        mapToObject = new MapToDBObject<T>(clazz, handler);
        convert = new BasicRowProcessor();
    }

    @Override
    public List<T> handle(ResultSet rs) throws SQLException {
        List<T> rows = new ArrayList<T>();
        while (rs.next()) {
            T tmp = this.handleRow(rs);
            if (tmp != null)
                rows.add(tmp);
        }
        return rows;
    }

    private T handleRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = this.convert.toMap(rs);
        return mapToObject.toObject(map);
    }
}
