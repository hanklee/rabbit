/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:20
 */
package com.lixianling.rabbit;

/**
 *
 * database operation exception
 *
 * @author Xianling Li(hanklee)
 * $Id: DBException.java 36 2016-01-06 17:24:04Z hank $
 */
public class DBException extends Exception {

    private String reason;

    public DBException(String reason) {
        super(reason);
        this.reason = reason;
    }

    public String reason(){
        return this.reason;
    }
}
