/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:06
 */
package com.lixianling.rabbit.manager;

import com.lixianling.rabbit.conf.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import redis.clients.jedis.Protocol;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Xianling Li(hanklee)
 * $Id: RabbitManager.java 40 2016-01-08 17:11:07Z hank $
 */
public final class RabbitManager {

    private static final String RABBIT_CONF_FILE = "rabbit.xml";
    private static final String DBOBJECT_CONF_FILE = "DBObject.xml";

    public static final RabbitConfig RABBIT_CONFIG;

    static {
        RABBIT_CONFIG = readConfig();
        init();
    }

    private RabbitManager() {
    }

    private static void init() {
        if (RabbitManager.RABBIT_CONFIG.mode.contains("redis")) {
            // register redis
            RedisManager.register();
        }

        if (RabbitManager.RABBIT_CONFIG.mode.contains("mysql")) {
            // register datasource
            try {
                DataSourceManager.register();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        if (RabbitManager.RABBIT_CONFIG.mode.contains("elast")) {
            // register elastic
            ElasticManager.register();
        }

        if (RabbitManager.RABBIT_CONFIG.mode.contains("mongo")) {
            // register mongodb
            MongoManager.register();
        }

        // register db object
        DBObjectManager.register();
    }

    public static void register(){
        // nothing
    }


    /**
     * read rabbit.xml  数据库连接配置文件和缓存配置文件
     * <p/>
     * read DBObject.xml  数据库表与对象相关连的配置文件
     *
     * @return RabbitConfig
     */
    private static RabbitConfig readConfig() {
        RabbitConfig rabbitConfig = new RabbitConfig();
        rabbitConfig.dbObjectConfig = new DBObjectConfig();
        rabbitConfig.jsonTableConfig = new TableConfig();

        rabbitConfig.dataSources = new HashMap<String, DataSourceConfig>();
        try {
            Document dd = parseXML(RABBIT_CONF_FILE, false);

            NodeList list = dd.getElementsByTagName("datasource");
            for (int i = 0; i < list.getLength(); i++) {
                Element tE = (Element) list.item(i);
                String name = tE.getAttribute("name");
                String def = tE.getAttribute("default");
                DataSourceConfig df = new DataSourceConfig();
                df.name = name;
                if ("true".equals(def)) {
                    df._default = true;
                }
                NodeList subList = tE.getElementsByTagName("url");
                if (subList.getLength() > 0) {
                    df.url = subList.item(0).getTextContent().trim();
                }

                subList = tE.getElementsByTagName("user");
                if (subList.getLength() > 0) {
                    df.user = subList.item(0).getTextContent().trim();
                }

                subList = tE.getElementsByTagName("password");
                if (subList.getLength() > 0) {
                    df.password = subList.item(0).getTextContent().trim();
                }

                subList = tE.getElementsByTagName("driver");
                if (subList.getLength() > 0) {
                    df.driver = subList.item(0).getTextContent().trim();
                }
                rabbitConfig.dataSources.put(name, df);
            }

            list = dd.getElementsByTagName("mode");
            if (list.getLength() > 0) {
                String model = list.item(0).getTextContent().trim();
                rabbitConfig.mode = model;
                // if ("REDIS".equals(model)) {
                //     rabbitConfig.mode = RabbitConfig.Mode.REDIS;
                // } else if ("MIX".equals(model)) {
                //     rabbitConfig.mode = RabbitConfig.Mode.MIX;
                // } else {
                //     rabbitConfig.mode = RabbitConfig.Mode.MYSQL;
                // }
            }

            list = dd.getElementsByTagName("source");
            if (list.getLength() > 0) {
                String source = list.item(0).getTextContent().trim();
                rabbitConfig.source = source;
            }

            RedisConfig redisConfig = new RedisConfig();

            list = dd.getElementsByTagName("redis");

            if (list.getLength() > 0) {
                Element tE = (Element) list.item(0);

                NodeList subList = tE.getElementsByTagName("cluster");
                redisConfig.cluster = subList.getLength() > 0 && "true".equals(subList.item(0).getTextContent());

                redisConfig.hosts = new ArrayList<RedisConfig.Host>();
                subList = tE.getElementsByTagName("host");
                for (int i = 0; i < subList.getLength(); i++) {
                    RedisConfig.Host host = new RedisConfig.Host();
                    Element hostE = (Element) subList.item(i);
                    host.port = Integer.valueOf(hostE.getAttribute("port"));
                    if (hostE.hasAttribute("index")) {
                        host.index = Integer.valueOf(hostE.getAttribute("index"));
                    } else {
                        host.index = Protocol.DEFAULT_DATABASE;
                    }
                    if (hostE.hasAttribute("password")) {
                        host.password = hostE.getAttribute("password");
                    } else {
                        host.password = null;
                    }
                    host.host = hostE.getTextContent();
                    redisConfig.hosts.add(host);
                }
            }
            rabbitConfig.redisConfig = redisConfig;

            ElasticConfig elasticConfig = new ElasticConfig();
            list = dd.getElementsByTagName("elastic");

            if (list.getLength() > 0) {
                Element tE = (Element) list.item(0);
                NodeList subList = tE.getElementsByTagName("setting");
                for (int i = 0; i < subList.getLength(); i++) {
                    Element setE = (Element) subList.item(i);
                    String key = setE.getElementsByTagName("name").item(0).getTextContent().trim();
                    String value = setE.getElementsByTagName("value").item(0).getTextContent().trim();
                    elasticConfig.settings.put(key, value);
                }

                subList = tE.getElementsByTagName("host");
                for (int i = 0; i < subList.getLength(); i++) {
                    ElasticConfig.Host host = new ElasticConfig.Host();
                    Element hostE = (Element) subList.item(i);
                    host.port = Integer.valueOf(hostE.getAttribute("port"));
                    host.host = hostE.getTextContent().trim();
                    elasticConfig.hosts.add(host);
                }
            }
            rabbitConfig.elasticConfig = elasticConfig;

            MongoConfig mongoConfig = new MongoConfig();
            list = dd.getElementsByTagName("mongo");

            if (list.getLength() > 0) {
                Element tE = (Element) list.item(0);
                NodeList subList = tE.getElementsByTagName("host");
                for (int i = 0; i < subList.getLength(); i++) {
                    MongoConfig.Host host = new MongoConfig.Host();
                    Element hostE = (Element) subList.item(i);
                    host.port = Integer.valueOf(hostE.getAttribute("port"));
                    host.host = hostE.getTextContent().trim();
                    mongoConfig.hosts.add(host);
                }
            }
            rabbitConfig.mongoConfig = mongoConfig;


            Document objectXML = parseXML(DBOBJECT_CONF_FILE, false);

            list = objectXML.getElementsByTagName("dbobject");
            for (int i = 0; i < list.getLength(); i++) {
                Element tE = (Element) list.item(i);
                DBObjectConfig.DBObjectSet dbset = new DBObjectConfig.DBObjectSet();
                NodeList subList = tE.getElementsByTagName("class_name");
                if (subList.getLength() > 0) {
                    Element classE = (Element) subList.item(0);
                    NodeList subList2 = tE.getElementsByTagName("table_name");
                    Element tableE = (Element) subList2.item(0);

                    String class_name = classE.getTextContent();
                    String table_name = tableE.getTextContent();
                    String cMark = classE.getAttribute("mark");
                    String sources = classE.getAttribute("sources");
                    String tMark = tableE.getAttribute("mark");
                    dbset.class_name = class_name.trim();
                    dbset.table_name = table_name.trim();
                    dbset.table_sources = sources;
                    dbset.mark_class = cMark;
                    dbset.mark_table = tMark;
                }

                rabbitConfig.dbObjectConfig.dbObjectSets.add(dbset);
            }

            list = objectXML.getElementsByTagName("jsontable");
            for (int i = 0; i < list.getLength(); i++) {
                Element tE = (Element) list.item(i);
                TableConfig.TableObject jTable = new TableConfig.TableObject();
                NodeList subList = tE.getElementsByTagName("table_name");
                if (subList.getLength() > 0) {
                    Element tableE = (Element) subList.item(0);
                    jTable.table_name = tableE.getTextContent().trim();
                }

                NodeList subList3 = tE.getElementsByTagName("table_field");
                if (subList3.getLength() > 0) {
                    jTable.table_field = subList3.item(0).getTextContent().trim();
                } else {
                    jTable.table_field = "";
                }

                subList3 = tE.getElementsByTagName("table_source");
                if (subList3.getLength() > 0) {
                    jTable.table_source = subList3.item(0).getTextContent().trim();
                } else {
                    jTable.table_source = "";
                }

                subList3 = tE.getElementsByTagName("incr_field");
                if (subList3.getLength() > 0) {
                    jTable.incr_field = subList3.item(0).getTextContent().trim();
                } else {
                    jTable.incr_field = "";
                }

                subList3 = tE.getElementsByTagName("key_field");
                if (subList3.getLength() > 0) {
                    jTable.key_field = subList3.item(0).getTextContent().trim();
                } else {
                    jTable.key_field = "";
                }

                rabbitConfig.jsonTableConfig.jsontables.add(jTable);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rabbitConfig;
    }

    private static Document parseXML(String filename,
                                     boolean validating) throws Exception {
        // Create a builder factory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(validating);

        // Prevent expansion of entity references
        factory.setExpandEntityReferences(false);

        // Create the builder and parse the file
        InputStream in = getInputStream(filename);
        Document doc = factory.newDocumentBuilder().parse(in);
        in.close();
        return doc;
    }

    private static InputStream getInputStream(String filename) {
        ClassLoader cL = Thread.currentThread().getContextClassLoader();
        if (cL == null) {
            cL = RabbitManager.class.getClassLoader();
        }
        return cL.getResourceAsStream(filename);
    }


    public static void main(String[] args) {
//        File file = new File(".");
//        System.out.println(file.getAbsolutePath());
        System.out.println(RABBIT_CONFIG.mode);
        Map<String, DataSourceConfig> data = RABBIT_CONFIG.dataSources;
        for (String key : data.keySet()) {
            System.out.println(key);
            DataSourceConfig conf = data.get(key);
            System.out.println(conf.url);
            System.out.println(conf.driver);
            System.out.println(conf._default + "," + conf.password + "," + conf.user);
        }

        if (RABBIT_CONFIG.redisConfig != null) {
            System.out.println(RABBIT_CONFIG.redisConfig.hosts.size());
        }

    }
}
