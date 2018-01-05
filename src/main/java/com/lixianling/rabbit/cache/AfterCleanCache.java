/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:19
 */
package com.lixianling.rabbit.cache;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: AfterCleanCache.java 35 2016-01-06 15:27:41Z hank $
 */
public interface AfterCleanCache {

    void cleanKeyCache(String key);
}
