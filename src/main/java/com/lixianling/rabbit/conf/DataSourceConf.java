/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:10
 */
package com.lixianling.rabbit.conf;

import java.util.Map;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: DataSourceConf.java 35 2016-01-06 15:27:41Z hank $
 */
public class DataSourceConf {
    public String driver;
    public String name;
    public String url;
    public String user;
    public String password;
    public boolean _default;

    // table and class relationship
    public Map<String, String> tableToClass;
    public Map<String, String> tableExcludes;
//    public Map<String, String> tableToCacheKeyField;
    public Map<String, String> classToTable;
}
