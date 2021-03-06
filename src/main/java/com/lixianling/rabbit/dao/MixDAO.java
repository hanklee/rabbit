/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 23:12
 */
package com.lixianling.rabbit.dao;

import com.lixianling.rabbit.DBException;

/**
 *
 * cache dao and real operation dao mix together
 *
 * @author Xianling Li(hanklee)
 *         $Id: MixDAO.java 38 2016-01-07 17:07:06Z hank $
 */
public abstract class MixDAO extends DAO {

    public MixDAO(String source) {
        super(source);
    }

    // SYNC operation

    public abstract void syncObject(String table,Object ... objs) throws DBException;

    public abstract void syncTable(String table) throws DBException;

    // NO clean operation

}
