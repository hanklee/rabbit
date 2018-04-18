/**
 * Create time: 11-Jan-2018
 */
package com.lixianling.rabbit.manager;

import com.lixianling.rabbit.conf.ElasticConfig;
import com.lixianling.rabbit.conf.RabbitConfig;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hank
 */
public final class ElasticManager {

    private static final ElasticManager INSTANCE;

    static {
        // just init cache manager
        INSTANCE = new ElasticManager(RabbitManager.RABBIT_CONFIG);
    }

    private Settings settings;
    private List<ElasticConfig.Host> hosts;
    private TransportClient client;

    public static ElasticManager getInstance() {
        return INSTANCE;
    }

    protected static void register() {
        // nothing to do
    }

    private ElasticManager() {

    }

    private ElasticManager(RabbitConfig config) {
        Settings.Builder settingsBuilder = Settings.builder();
        for (String key : config.elasticConfig.settings.keySet()) {
            String value = config.elasticConfig.settings.get(key);
            settingsBuilder.put(key,value);
        }
//        this.settings = settingsBuilder.put("transport.type", "netty3").put("http.type", "netty3").build();
        this.settings = settingsBuilder.build();
        this.hosts = new ArrayList<ElasticConfig.Host>();
        this.hosts.addAll(config.elasticConfig.hosts);
        this.client = new PreBuiltTransportClient(this.settings);
        //TransportClient transportClient = TransportClient.builder().settings(settings).build();
        for (ElasticConfig.Host host : this.hosts) {
            try {
                this.client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host.host), host.port));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

    }

    public TransportClient getClient() {
        return this.client;
    }
}
