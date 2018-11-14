/**
 * Create time: 2018-11-14
 */
package com.lixianling.rabbit.obj;

import com.lixianling.rabbit.DBObject;

/**
 * @author Xianling Li
 */
public class PrivObject1 extends DBObject {
    private int id;
    private int time;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
