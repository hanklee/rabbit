/**
 * Create time: 11-Jan-2018
 */
package com.lixianling.rabbit.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hank
 */
public class ElasticConfig {
    public Map<String,String> settings = new HashMap<String,String>();
    public List<Host> hosts = new ArrayList<Host>();

    public static class Host {
        public String host;
        public int port;
//        public String password;
    }

}
