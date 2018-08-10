/**
 * Create time: 2018-08-10
 */
package com.lixianling.rabbit.conf;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xianling Li
 */
public class TableConfig {

    public List<TableObject> jsontables = new ArrayList<TableObject>();

    public static class TableObject {
        public String table_name;
        public String table_field;
        public String incr_field;
        //        public String exclude_field; // remove field, using update exclude_columns
        public String key_field;
    }
}
