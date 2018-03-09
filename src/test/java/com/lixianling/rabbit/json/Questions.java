/**
 * Create time: 23-Jan-2018
 */
package com.q4answer.dolphin.data;

import com.lixianling.rabbit.DBObject;

import java.util.List;

/**
 *
 * @author hank
 */
public class Questions extends DBObject {

    public String title;
    public String content;
    public Long uid; // crawl
    public String keywords;
    public List<Integer> tags;
    public int create_at;
}
