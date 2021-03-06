/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 22:51
 */
package com.lixianling.rabbit.manager;

import com.lixianling.rabbit.conf.RabbitConfig;
import com.lixianling.rabbit.conf.RedisConfig;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * @author Xianling Li(hanklee)
 * $Id: RedisManager.java 41 2016-01-09 17:39:32Z hank $
 */
public final class RedisManager {
    private static final JedisPool pool;
//    private static final MixJedisCommands commands;

    private static String source;

    static {
        // just init cache manager
        pool = init(RabbitManager.RABBIT_CONFIG);
//        commands = new RabbitJedisCommands(pool);
    }

    protected static void register() {
        // nothing to do
    }

    private RedisManager() {
    }

    private static JedisPool init(final RabbitConfig config) {
        if (config.mode.contains("redis")) {
//            System.err.println("Using redis to record operation data, mysql backup the data!");
            if (!config.redisConfig.cluster) {
                RedisConfig.Host host = config.redisConfig.hosts.get(0);
//                System.err.println("AUTH password:" + config.password);
                return init(host.host, host.port, host.index, host.password);
            }
        }
        return null;
    }


    public static JedisPool init(String ip, int port, int db, String pwd) {
        JedisPoolConfig config = new JedisPoolConfig();
        if (pwd == null || pwd.trim().length() == 0) {
            pwd = null;
        }

        source = String.valueOf(db);

        config.setMaxTotal(200);
        config.setMaxIdle(50);
        config.setMinIdle(8);//设置最小空闲数
        config.setMaxWaitMillis(10000);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
//Idle时进行连接扫描
        config.setTestWhileIdle(true);
//表示idle object evitor两次扫描之间要sleep的毫秒数
        config.setTimeBetweenEvictionRunsMillis(30000);
//表示idle object evitor每次扫描的最多的对象数
        config.setNumTestsPerEvictionRun(10);
//表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
        config.setMinEvictableIdleTimeMillis(60000);


//        //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
//        //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
//        config.setMaxTotal(500);
//        //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
//        config.setMaxIdle(5);
//        //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
////            config.setMaxWait(1000 * 10);
//        config.setMaxWaitMillis(1000 * 10);
//        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
//        config.setTestOnBorrow(true);
        return new JedisPool(config, ip, port, Protocol.DEFAULT_TIMEOUT, pwd, db);
//            commands = new RabbitJedis(pool);
    }

//    public static void initCluster(CacheConfig config) {
//        Set<HostAndPort> hostAndPorts = new HashSet<HostAndPort>();
//        for (CacheConfig.Host host : config.hosts) {
//            hostAndPorts.add(new HostAndPort(host.host, host.port));
//        }
//
//        RabbitJedisCluster cluster = new RabbitJedisCluster(hostAndPorts);
//        cluster.auth(config.password);
//        commands = cluster;
//
//        // todo get pools
//    }

    /**
     * 构建redis连接池
     *
     * @return JedisPool
     */
    public static JedisPool getPool() {
        return pool;
    }

    public static String getSourcel() {
        return source;
    }


    /**
     * 获取数据
     *
     * @param key
     * @return
     */
//    public static String get(final String key) {
//        return commands.get(key);
//    }

//    public static void del(final String key) {
//        commands.del(key);
//    }
//
//    public static void delKeyPattern(final String parttern) {
//        try {
//            new JedisExecute<Long>(pool) {
//                @Override
//                public Long execute(Jedis jedis) throws RedisException {
//                    Set<String> result = jedis.keys(parttern);
//                    if (result.size() > 0)
//                        jedis.del(result.toArray(new String[result.size()]));
//                    return 0L;
//                }
//            }.run();
//        } catch (com.lixianling.rabbit.dao.redis.RedisException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void dels(final Set<String> keys) {
//        if (keys.size() > 0)
//            commands.del(keys.toArray(new String[keys.size()]));
//    }
//
//    public static void set(final String key, final String value) {
//        commands.set(key, value);
//    }
//
//    public static Set<String> keys(final String parttern) {
//        return commands.keys(parttern);
//    }
}
