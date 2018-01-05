/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-07 01:08
 */
package com.lixianling.rabbit.dao.sql;

import java.util.Map;

/**
 *
 *  for data base translate Map to Object
 *
 * @author Xianling Li(hanklee)
 *         $Id: MapToObject.java 36 2016-01-06 17:24:04Z hank $
 */
public interface MapToObject<E> {

    E toObject(Map<String, Object> map);

}
