/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 22:55
 */
package com.lixianling.rabbit.cache;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;

import java.io.Serializable;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: CacheItem.java 37 2016-01-06 19:08:34Z hank $
 */
public class CacheItem implements Serializable {

    public DBObject obj;

    public String table_name;

    public CacheItem(String table, DBObject obj) throws DBException {
        this.obj = obj;
        this.table_name = table;
    }
}
