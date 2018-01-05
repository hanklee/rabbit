/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-07 01:07
 */
package com.lixianling.rabbit.dao.sql;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: MapToObjectHandler.java 36 2016-01-06 17:24:04Z hank $
 */
public interface MapToObjectHandler<T> {

    boolean handler(T o);
}
