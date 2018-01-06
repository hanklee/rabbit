/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 21:06
 */
package com.lixianling.rabbit.manager;

import com.lixianling.rabbit.conf.DataSourceConf;
import com.lixianling.rabbit.conf.RabbitConfig;
import com.lixianling.rabbit.conf.RedisConfig;
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
 *         $Id: RabbitManager.java 40 2016-01-08 17:11:07Z hank $
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
        // register datasource
        DataSourceManager.register();

        // register db object
        DBObjectManager.register();

        // register redis
        RedisManager.register();

        // register cahce
        RedisManager.register();
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
        Map<String, DataSourceConf> dataSources = new HashMap<String, DataSourceConf>();
        try {
            Document dd = parseXML(RABBIT_CONF_FILE, false);

            NodeList list = dd.getElementsByTagName("datasource");
            String defaultName = null;
            for (int i = 0; i < list.getLength(); i++) {
                Element tE = (Element) list.item(i);
                String name = tE.getAttribute("name");
                String def = tE.getAttribute("default");
                DataSourceConf df = new DataSourceConf();
                df.name = name;
                if (def != null && "true".equals(def)) {
                    df._default = true;
                    defaultName = name;
                }
                NodeList subList = tE.getElementsByTagName("url");
                if (subList.getLength() > 0) {
                    df.url = subList.item(0).getTextContent();
                }

                subList = tE.getElementsByTagName("user");
                if (subList.getLength() > 0) {
                    df.user = subList.item(0).getTextContent();
                }

                subList = tE.getElementsByTagName("password");
                if (subList.getLength() > 0) {
                    df.password = subList.item(0).getTextContent();
                }

                subList = tE.getElementsByTagName("driver");
                if (subList.getLength() > 0) {
                    df.driver = subList.item(0).getTextContent();
                }

                df.tableToClass = new HashMap<String, String>();
                df.tableExcludes = new HashMap<String, String>();
                df.classToTable = new HashMap<String, String>();
                df.tableToCacheKeyField = new HashMap<String, String>();
                dataSources.put(name, df);
            }

            list = dd.getElementsByTagName("mode");
            if (list.getLength() > 0) {
                String model = list.item(0).getTextContent();
                if ("REDIS".equals(model)) {
                    rabbitConfig.mode = RabbitConfig.Mode.REDIS;
                } else if ("MIX".equals(model)) {
                    rabbitConfig.mode = RabbitConfig.Mode.MIX;
                } else {
                    rabbitConfig.mode = RabbitConfig.Mode.MYSQL;
                }
            }

//            list = dd.getElementsByTagName("cache");
//            if (list.getLength() > 0) {
//                Element element = (Element) list.item(0);
//                if (element.hasChildNodes()){
//                    CacheConfig cacheConfig = new CacheConfig();
//                    cacheConfig.cache = CacheManager.REDIS_CACHE_NAME;
//                    cacheConfig.afterCacheClass = null;
//                    list = dd.getElementsByTagName("afterCacheClass");
//                    if (list.getLength() > 0) {
//                        String afterClass = list.item(0).getTextContent();
//                        if (afterClass.length() > 0) {
//                            try {
//                                cacheConfig.afterCacheClass = Class.forName(afterClass);
//                            } catch (Exception ex) {
////                        rabbitConfig.afterCacheClass = null;
//                            }
////                    System.out.println("after cache class:"+afterClass);
//                        }
//                    }
//                    NodeList subList = dd.getElementsByTagName("key_prefix");
//                    if (subList.getLength() > 0) {
//                        cacheConfig.cachePrefix = subList.item(0).getTextContent();
//                    }
//                    subList = dd.getElementsByTagName("sync_time");
//                    if (subList.getLength() > 0) {
//                        cacheConfig.syncTime = Long.valueOf(subList.item(0).getTextContent());
//                    } else {
//                        cacheConfig.syncTime = CacheConfig.DEFAULT.syncTime;
//                    }
//                    rabbitConfig.cacheConfig = cacheConfig;
//                } else {
//                    rabbitConfig.cacheConfig = CacheConfig.DEFAULT;
//                }
//            }

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

            rabbitConfig.redisConfig.classToTable = new HashMap<String, String>();
            rabbitConfig.redisConfig.tableField = new HashMap<String, String>();
            rabbitConfig.redisConfig.tableUniqueField = new HashMap<String, String>();
            rabbitConfig.redisConfig.tableIncrField = new HashMap<String, String>();
            rabbitConfig.redisConfig.tableKeyField = new HashMap<String, String>();
            rabbitConfig.redisConfig.tableToClass = new HashMap<String, String>();


            Document objectXML = parseXML(DBOBJECT_CONF_FILE, false);

            list = objectXML.getElementsByTagName("dbobject");

            for (int i = 0; i < list.getLength(); i++) {
                Element tE = (Element) list.item(i);
                if (rabbitConfig.mode == RabbitConfig.Mode.MIX
                        || rabbitConfig.mode == RabbitConfig.Mode.MYSQL) {
                    if (rabbitConfig.mode == RabbitConfig.Mode.MIX) {
                        registerObj2Redis(tE,rabbitConfig);
                    }
                    String tredis = tE.getAttribute("redis");
                    if ("true".equals(tredis)) {
                        continue;
                    }
                    String tDatasource = tE.getAttribute("datasource");
                    if (tDatasource == null || tDatasource.length() < 1) {
                        tDatasource = defaultName;
                    }
                    DataSourceConf df = dataSources.get(tDatasource);
                    NodeList subList = tE.getElementsByTagName("class_name");
                    if (subList.getLength() > 0) {
                        String class_name = subList.item(0).getTextContent();
                        NodeList subList2 = tE.getElementsByTagName("table_name");
                        Element tableE = (Element) subList2.item(0);
                        String tMark = tableE.getAttribute("mark");
                        String table_name = tableE.getTextContent();
                        df.tableToClass.put(table_name, class_name);
                        if ("true".equals(tMark)) {
                            df.classToTable.put(class_name, table_name);
                        }

                        NodeList subList3 = tE.getElementsByTagName("exclude_field");

                        if (subList3.getLength() > 0) {
                            String exclude_field = subList3.item(0).getTextContent();
                            df.tableExcludes.put(table_name, exclude_field);
                        } else {
                            df.tableExcludes.put(table_name, "");
                        }

                        NodeList subList4 = tE.getElementsByTagName("cache_key");
                        if (subList4.getLength() > 0) {
                            String key_field = subList4.item(0).getTextContent();
                            df.tableToCacheKeyField.put(table_name, key_field);
                        } else {
                            df.tableToCacheKeyField.put(table_name, RabbitConfig.DEFAULT_CACHE_KEY_FIELD);
                        }
                    }
                } else {
                    registerObj2Redis(tE,rabbitConfig);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        rabbitConfig.dataSources = dataSources;
        return rabbitConfig;
    }

    private static void registerObj2Redis(Element tE,RabbitConfig rabbitConfig) throws Exception{
        NodeList subList = tE.getElementsByTagName("class_name");
        if (subList.getLength() > 0) {
            String class_name = subList.item(0).getTextContent();
            NodeList subList2 = tE.getElementsByTagName("table_name");
            Element tableE = (Element) subList2.item(0);
            String tMark = tableE.getAttribute("mark");
            String table_name = tableE.getTextContent();
            rabbitConfig.redisConfig.tableToClass.put(table_name, class_name);
            if ("true".equals(tMark)) {
                rabbitConfig.redisConfig.classToTable.put(class_name, table_name);
            }

            NodeList subList3 = tE.getElementsByTagName("table_field");

            if (subList3.getLength() > 0) {
                String exclude_field = subList3.item(0).getTextContent();
                rabbitConfig.redisConfig.tableField.put(table_name, exclude_field);
            } else {
                rabbitConfig.redisConfig.tableField.put(table_name, "");
            }

            subList3 = tE.getElementsByTagName("unique_field");

            if (subList3.getLength() > 0) {
                String exclude_field = subList3.item(0).getTextContent();
                rabbitConfig.redisConfig.tableUniqueField.put(table_name, exclude_field);
            } else {
                rabbitConfig.redisConfig.tableUniqueField.put(table_name, "");
            }

            subList3 = tE.getElementsByTagName("incr_field");

            if (subList3.getLength() > 0) {
                String exclude_field = subList3.item(0).getTextContent();
                rabbitConfig.redisConfig.tableIncrField.put(table_name, exclude_field);
            } else {
                rabbitConfig.redisConfig.tableIncrField.put(table_name, "");
            }

            subList3 = tE.getElementsByTagName("key_field");

            if (subList3.getLength() > 0) {
                String exclude_field = subList3.item(0).getTextContent();
                rabbitConfig.redisConfig.tableKeyField.put(table_name, exclude_field);
            } else {
                rabbitConfig.redisConfig.tableKeyField.put(table_name, "");
            }
        }
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
        Map<String, DataSourceConf> data = RABBIT_CONFIG.dataSources;
        for (String key : data.keySet()) {
            System.out.println(key);
            DataSourceConf conf = data.get(key);
            System.out.println(conf.url);
            System.out.println(conf.driver);
            System.out.println(conf._default + "," + conf.password + "," + conf.user);
        }

        if (RABBIT_CONFIG.redisConfig != null) {
            System.out.println(RABBIT_CONFIG.redisConfig.hosts.size());
        }

    }
}
