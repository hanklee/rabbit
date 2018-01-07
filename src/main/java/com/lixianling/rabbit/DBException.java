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

    public static final int CODE_NODEFINE = 0;
    public static final int CODE_NOTFOUND = 1;
    public static final int CODE_EXIST_VALUE = 2;

    private String reason;
    private int code;

    public DBException(String reason) {
        super(reason);
        this.reason = reason;
        this.code = CODE_NODEFINE;
    }

    public DBException(String reason,int code) {
        super(reason);
        this.reason = reason;
        this.code = code;
    }

    public String reason(){
        return this.reason;
    }

    public int code(){
        return this.code;
    }
}
