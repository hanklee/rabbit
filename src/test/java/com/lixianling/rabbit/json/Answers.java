/**
 * Create time: 23-Jan-2018
 */
package com.q4answer.dolphin.data;

import com.lixianling.rabbit.DBObject;

/**
 *
 * @author hank
 */
public class Answers extends DBObject {

    public int create_at;
    public String content;
    public Long uid;
    public String md5;
}
