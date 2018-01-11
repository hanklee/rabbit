/**
 * Create time: 12-Jan-2018
 */
package com.lixianling.rabbit.conf;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hank
 */
public class MongoConfig {

    public List<Host> hosts = new ArrayList<Host>();

    public static class Host {
        public String host;
        public int port;
    }
}
