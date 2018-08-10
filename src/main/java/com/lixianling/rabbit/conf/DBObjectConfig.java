/**
 * Create time: 08-Jan-2018
 */
package com.lixianling.rabbit.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hank
 */
public class DBObjectConfig {

    public List<DBObjectSet> dbObjectSets = new ArrayList<DBObjectSet>();

    public static class DBObjectSet{
        public String class_name;
        public String datasource;
        public String mode;
        public String table_name;
        public String mark_table;
        public String mark_class;

        public String table_field;
        public String incr_field;
//        public String exclude_field; // remove field, using update exclude_columns
        public String key_field;
    }


}
