/**
 * Create time: 11-Jan-2018
 */
package com.lixianling.rabbit.manager;

import com.lixianling.rabbit.conf.MongoConfig;
import com.lixianling.rabbit.conf.RabbitConfig;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hank
 */
public final class MongoManager {


    private MongoClient client;


    private static final MongoManager INSTANCE;

    static {
        // just init cache manager
        INSTANCE = new MongoManager(RabbitManager.RABBIT_CONFIG);
    }

    public static MongoManager getInstance() {
        return INSTANCE;
    }

    protected static void register() {
        // nothing to do
    }

    public MongoManager(RabbitConfig config) {
        List<ServerAddress> addresses = new ArrayList<ServerAddress>();
        for(MongoConfig.Host host:config.mongoConfig.hosts) {
            ServerAddress address = new ServerAddress(host.host,host.port);
            addresses.add(address);
        }
        this.client = new MongoClient(addresses);
    }

    public MongoClient getClient() {
        return client;
    }
}
