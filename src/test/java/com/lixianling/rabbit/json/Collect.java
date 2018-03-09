/**
 * Create time: 23-Jan-2018
 */
package com.q4answer.dolphin.data;

import com.lixianling.rabbit.DBObject;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hank
 */
public class Collect extends DBObject {

    public ObjectId _id;

    public String title;
    public String des;
    public String bs;
    public String keywords;
    public Long crawl;
    public List<String> answers;
    public List<Integer> tags;

    public void copyQiqu(Qiqu qiqu) {
//        this._id = qiqu._id;
        this.title = qiqu.title;
        this.des = qiqu.des;
        this.bs = qiqu.bs;
        this.keywords = qiqu.keywords;
        this.crawl = qiqu.crawl;
        answers = new ArrayList<>();
        answers.addAll(qiqu.answers);
        tags = new ArrayList<>();
        tags.addAll(qiqu.tags);
    }
}
