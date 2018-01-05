/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-06 23:05
 */
package com.lixianling.rabbit.dao.redis;

import redis.clients.jedis.*;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Xianling Li(hanklee)
 *         $Id: RabbitJedisCommands.java 41 2016-01-09 17:39:32Z hank $
 */
public class RabbitJedisCommands implements MixJedisCommands {
    private JedisPool pool;

    public RabbitJedisCommands(JedisPool pool) {
        this.pool = pool;
    }

    @Override
    public String set(final String key, final String value) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.set(key, value);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String set(final String key, final String value, final String nxxx, final String expx, final long time) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.set(key, value, nxxx, expx, time);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String set(String s, String s1, String s2) {
        return null;
    }

    @Override
    public String get(final String key) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.get(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean exists(final String key) {
        try {
            return new JedisExecute<Boolean>(pool) {
                @Override
                public Boolean execute(Jedis connection) throws RedisException {
                    return connection.exists(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long persist(final String key) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.persist(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String type(final String key) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.type(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long expire(final String key, final int seconds) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.expire(key, seconds);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long pexpire(String s, long l) {
        return null;
    }

    @Override
    public Long expireAt(final String key, final long unixTime) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.expireAt(key, unixTime);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long pexpireAt(String s, long l) {
        return null;
    }

    @Override
    public Long ttl(final String key) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.ttl(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long pttl(String s) {
        return null;
    }

    @Override
    public Boolean setbit(final String key, final long offset, final boolean value) {
        try {
            return new JedisExecute<Boolean>(pool) {
                @Override
                public Boolean execute(Jedis connection) throws RedisException {
                    return connection.setbit(key, offset, value);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean setbit(final String key, final long offset, final String value) {
        try {
            return new JedisExecute<Boolean>(pool) {
                @Override
                public Boolean execute(Jedis connection) throws RedisException {
                    return connection.setbit(key, offset, value);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean getbit(final String key, final long offset) {
        try {
            return new JedisExecute<Boolean>(pool) {
                @Override
                public Boolean execute(Jedis connection) throws RedisException {
                    return connection.getbit(key, offset);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long setrange(final String key, final long offset, final String value) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.setrange(key, offset, value);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getrange(final String key, final long startOffset, final long endOffset) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.getrange(key, startOffset, endOffset);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getSet(final String key, final String value) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.getSet(key, value);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long setnx(final String key, final String value) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.setnx(key, value);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String setex(final String key, final int seconds, final String value) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.setex(key, seconds, value);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String psetex(String s, long l, String s1) {
        return null;
    }

    @Override
    public Long decrBy(final String key, final long integer) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.decrBy(key, integer);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long decr(final String key) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.decr(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long incrBy(final String key, final long integer) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.incrBy(key, integer);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Double incrByFloat(String s, double v) {
        return null;
    }

    @Override
    public Long incr(final String key) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.incr(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long append(final String key, final String value) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.append(key, value);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String substr(final String key, final int start, final int end) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.substr(key, start, end);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long hset(final String key, final String field, final String value) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.hset(key, field, value);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String hget(final String key, final String field) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.hget(key, field);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long hsetnx(final String key, final String field, final String value) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.hsetnx(key, field, value);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String hmset(final String key, final Map<String, String> hash) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.hmset(key, hash);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> hmget(final String key, final String... fields) {
        try {
            return new JedisExecute<List<String>>(pool) {
                @Override
                public List<String> execute(Jedis connection) throws RedisException {
                    return connection.hmget(key, fields);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long hincrBy(final String key, final String field, final long value) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.hincrBy(key, field, value);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Double hincrByFloat(String s, String s1, double v) {
        return null;
    }

    @Override
    public Boolean hexists(final String key, final String field) {
        try {
            return new JedisExecute<Boolean>(pool) {
                @Override
                public Boolean execute(Jedis connection) throws RedisException {
                    return connection.hexists(key, field);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long hdel(final String key, final String... field) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.hdel(key, field);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long hlen(final String key) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.hlen(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> hkeys(final String key) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.hkeys(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> hvals(final String key) {
        try {
            return new JedisExecute<List<String>>(pool) {
                @Override
                public List<String> execute(Jedis connection) throws RedisException {
                    return connection.hvals(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, String> hgetAll(final String key) {
        try {
            return new JedisExecute<Map<String, String>>(pool) {
                @Override
                public Map<String, String> execute(Jedis connection) throws RedisException {
                    return connection.hgetAll(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long rpush(final String key, final String... string) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.rpush(key, string);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long lpush(final String key, final String... string) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.lpush(key, string);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long llen(final String key) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.llen(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> lrange(final String key, final long start, final long end) {
        try {
            return new JedisExecute<List<String>>(pool) {
                @Override
                public List<String> execute(Jedis connection) throws RedisException {
                    return connection.lrange(key, start, end);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String ltrim(final String key, final long start, final long end) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.ltrim(key, start, end);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String lindex(final String key, final long index) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.lindex(key, index);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String lset(final String key, final long index, final String value) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.lset(key, index, value);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long lrem(final String key, final long count, final String value) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.lrem(key, count, value);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String lpop(final String key) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.lpop(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String rpop(final String key) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.rpop(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long sadd(final String key, final String... member) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.sadd(key, member);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> smembers(final String key) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.smembers(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long srem(final String key, final String... member) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.srem(key, member);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String spop(final String key) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.spop(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> spop(String s, long l) {
        return null;
    }

    @Override
    public Long scard(final String key) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.scard(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean sismember(final String key, final String member) {
        try {
            return new JedisExecute<Boolean>(pool) {
                @Override
                public Boolean execute(Jedis connection) throws RedisException {
                    return connection.sismember(key, member);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String srandmember(final String key) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.srandmember(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> srandmember(final String key, final int count) {
        try {
            return new JedisExecute<List<String>>(pool) {
                @Override
                public List<String> execute(Jedis connection) throws RedisException {
                    return connection.srandmember(key, count);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long strlen(final String key) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.strlen(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long zadd(final String key, final double score, final String member) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zadd(key, score, member);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long zadd(String s, double v, String s1, ZAddParams zAddParams) {
        return null;
    }

    @Override
    public Long zadd(final String key, final Map<String, Double> scoreMembers) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zadd(key, scoreMembers);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long zadd(String s, Map<String, Double> map, ZAddParams zAddParams) {
        return null;
    }

    @Override
    public Set<String> zrange(final String key, final long start, final long end) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.zrange(key, start, end);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long zrem(final String key, final String... member) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zrem(key, member);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Double zincrby(final String key, final double score, final String member) {
        try {
            return new JedisExecute<Double>(pool) {
                @Override
                public Double execute(Jedis connection) throws RedisException {
                    return connection.zincrby(key, score, member);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Double zincrby(String s, double v, String s1, ZIncrByParams zIncrByParams) {
        return null;
    }

    @Override
    public Long zrank(final String key, final String member) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zrank(key, member);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long zrevrank(final String key, final String member) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zrevrank(key, member);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> zrevrange(final String key, final long start, final long end) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.zrevrange(key, start, end);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<Tuple> zrangeWithScores(final String key, final long start, final long end) {
        try {
            return new JedisExecute<Set<Tuple>>(pool) {
                @Override
                public Set<Tuple> execute(Jedis connection) throws RedisException {
                    return connection.zrangeWithScores(key, start, end);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeWithScores(final String key, final long start, final long end) {
        try {
            return new JedisExecute<Set<Tuple>>(pool) {
                @Override
                public Set<Tuple> execute(Jedis connection) throws RedisException {
                    return connection.zrevrangeWithScores(key, start, end);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long zcard(final String key) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zcard(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Double zscore(final String key, final String member) {
        try {
            return new JedisExecute<Double>(pool) {
                @Override
                public Double execute(Jedis connection) throws RedisException {
                    return connection.zscore(key, member);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> sort(final String key) {
        try {
            return new JedisExecute<List<String>>(pool) {
                @Override
                public List<String> execute(Jedis connection) throws RedisException {
                    return connection.sort(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> sort(final String key, final SortingParams sortingParameters) {
        try {
            return new JedisExecute<List<String>>(pool) {
                @Override
                public List<String> execute(Jedis connection) throws RedisException {
                    return connection.sort(key, sortingParameters);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long zcount(final String key, final double min, final double max) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zcount(key, min, max);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long zcount(final String key, final String min, final String max) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zcount(key, min, max);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> zrangeByScore(final String key, final double min, final double max) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.zrangeByScore(key, min, max);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> zrangeByScore(final String key, final String min, final String max) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.zrangeByScore(key, min, max);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(final String key, final double max, final double min) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.zrevrangeByScore(key, max, min);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> zrangeByScore(final String key, final double min, final double max, final int offset, final int count) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.zrangeByScore(key, min, max, offset, count);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(final String key, final String max, final String min) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.zrevrangeByScore(key, max, min);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> zrangeByScore(final String key, final String min, final String max, final int offset, final int count) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.zrangeByScore(key, min, max, offset, count);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(final String key, final double max, final double min, final int offset, final int count) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.zrevrangeByScore(key, max, min, offset, count);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max) {
        try {
            return new JedisExecute<Set<Tuple>>(pool) {
                @Override
                public Set<Tuple> execute(Jedis connection) throws RedisException {
                    return connection.zrangeByScoreWithScores(key, min, max);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min) {
        try {
            return new JedisExecute<Set<Tuple>>(pool) {
                @Override
                public Set<Tuple> execute(Jedis connection) throws RedisException {
                    return connection.zrevrangeByScoreWithScores(key, max, min);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max, final int offset, final int count) {
        try {
            return new JedisExecute<Set<Tuple>>(pool) {
                @Override
                public Set<Tuple> execute(Jedis connection) throws RedisException {
                    return connection.zrevrangeByScoreWithScores(key, min, max, offset, count);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(final String key, final String max, final String min, final int offset, final int count) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.zrevrangeByScore(key, max, min, offset, count);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max) {
        try {
            return new JedisExecute<Set<Tuple>>(pool) {
                @Override
                public Set<Tuple> execute(Jedis connection) throws RedisException {
                    return connection.zrangeByScoreWithScores(key, min, max);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min) {
        try {
            return new JedisExecute<Set<Tuple>>(pool) {
                @Override
                public Set<Tuple> execute(Jedis connection) throws RedisException {
                    return connection.zrevrangeByScoreWithScores(key, max, min);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max, final int offset, final int count) {
        try {
            return new JedisExecute<Set<Tuple>>(pool) {
                @Override
                public Set<Tuple> execute(Jedis connection) throws RedisException {
                    return connection.zrangeByScoreWithScores(key, min, max, offset, count);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min, final int offset, final int count) {
        try {
            return new JedisExecute<Set<Tuple>>(pool) {
                @Override
                public Set<Tuple> execute(Jedis connection) throws RedisException {
                    return connection.zrevrangeByScoreWithScores(key, max, min, offset, count);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min, final int offset, final int count) {
        try {
            return new JedisExecute<Set<Tuple>>(pool) {
                @Override
                public Set<Tuple> execute(Jedis connection) throws RedisException {
                    return connection.zrevrangeByScoreWithScores(key, max, min, offset, count);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long zremrangeByRank(final String key, final long start, final long end) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zremrangeByRank(key, start, end);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long zremrangeByScore(final String key, final double start, final double end) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zremrangeByScore(key, start, end);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long zremrangeByScore(final String key, final String start, final String end) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zremrangeByScore(key, start, end);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long zlexcount(final String key, final String min, final String max) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zlexcount(key, min, max);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> zrangeByLex(final String key, final String min, final String max) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.zrangeByLex(key, min, max);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> zrangeByLex(final String key, final String min, final String max, final int offset, final int count) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.zrangeByLex(key, min, max, offset, count);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> zrevrangeByLex(String s, String s1, String s2) {
        return null;
    }

    @Override
    public Set<String> zrevrangeByLex(String s, String s1, String s2, int i, int i1) {
        return null;
    }

    @Override
    public Long zremrangeByLex(final String key, final String min, final String max) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zremrangeByLex(key, min, max);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long linsert(final String key, final BinaryClient.LIST_POSITION where, final String pivot, final String value) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.linsert(key, where, pivot, value);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long lpushx(final String key, final String... string) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.lpushx(key, string);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long rpushx(final String key, final String... string) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.rpushx(key, string);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> blpop(final String arg) {
        try {
            return new JedisExecute<List<String>>(pool) {
                @Override
                public List<String> execute(Jedis connection) throws RedisException {
                    return connection.blpop(arg);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> blpop(final int timeout, final String key) {
        try {
            return new JedisExecute<List<String>>(pool) {
                @Override
                public List<String> execute(Jedis connection) throws RedisException {
                    return connection.blpop(timeout, key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> brpop(final String arg) {
        try {
            return new JedisExecute<List<String>>(pool) {
                @Override
                public List<String> execute(Jedis connection) throws RedisException {
                    return connection.brpop(arg);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> brpop(final int timeout, final String key) {
        try {
            return new JedisExecute<List<String>>(pool) {
                @Override
                public List<String> execute(Jedis connection) throws RedisException {
                    return connection.brpop(timeout, key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long del(final String key) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.del(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String echo(final String string) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.echo(string);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long move(final String key, final int dbIndex) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.move(key, dbIndex);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long bitcount(final String key) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.bitcount(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long bitcount(final String key, final long start, final long end) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.bitcount(key, start, end);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long bitpos(String s, boolean b) {
        return null;
    }

    @Override
    public Long bitpos(String s, boolean b, BitPosParams bitPosParams) {
        return null;
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(final String key, final int cursor) {
        try {
            return new JedisExecute<ScanResult<Map.Entry<String, String>>>(pool) {
                @Override
                public ScanResult<Map.Entry<String, String>> execute(Jedis connection) throws RedisException {
                    return connection.hscan(key, cursor);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ScanResult<String> sscan(final String key, final int cursor) {
        try {
            return new JedisExecute<ScanResult<String>>(pool) {
                @Override
                public ScanResult<String> execute(Jedis connection) throws RedisException {
                    return connection.sscan(key, cursor);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ScanResult<Tuple> zscan(final String key, final int cursor) {
        try {
            return new JedisExecute<ScanResult<Tuple>>(pool) {
                @Override
                public ScanResult<Tuple> execute(Jedis connection) throws RedisException {
                    return connection.zscan(key, cursor);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(final String key, final String cursor) {
        try {
            return new JedisExecute<ScanResult<Map.Entry<String, String>>>(pool) {
                @Override
                public ScanResult<Map.Entry<String, String>> execute(Jedis connection) throws RedisException {
                    return connection.hscan(key, cursor);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String s, String s1, ScanParams scanParams) {
        return null;
    }

    @Override
    public ScanResult<String> sscan(final String key, final String cursor) {
        try {
            return new JedisExecute<ScanResult<String>>(pool) {
                @Override
                public ScanResult<String> execute(Jedis connection) throws RedisException {
                    return connection.sscan(key, cursor);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ScanResult<String> sscan(String s, String s1, ScanParams scanParams) {
        return null;
    }

    @Override
    public ScanResult<Tuple> zscan(final String key, final String cursor) {
        try {
            return new JedisExecute<ScanResult<Tuple>>(pool) {
                @Override
                public ScanResult<Tuple> execute(Jedis connection) throws RedisException {
                    return connection.zscan(key, cursor);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ScanResult<Tuple> zscan(String s, String s1, ScanParams scanParams) {
        return null;
    }

    @Override
    public Long pfadd(final String key, final String... elements) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.pfadd(key, elements);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long pfcount(final String key) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.pfcount(key);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @Override
    public Long geoadd(String s, double v, double v1, String s1) {
        return null;
    }

    @Override
    public Long geoadd(String s, Map<String, GeoCoordinate> map) {
        return null;
    }

    @Override
    public Double geodist(String s, String s1, String s2) {
        return null;
    }

    @Override
    public Double geodist(String s, String s1, String s2, GeoUnit geoUnit) {
        return null;
    }

    @Override
    public List<String> geohash(String s, String... strings) {
        return null;
    }

    @Override
    public List<GeoCoordinate> geopos(String s, String... strings) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadius(String s, double v, double v1, double v2, GeoUnit geoUnit) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadius(String s, double v, double v1, double v2, GeoUnit geoUnit, GeoRadiusParam geoRadiusParam) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMember(String s, String s1, double v, GeoUnit geoUnit) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMember(String s, String s1, double v, GeoUnit geoUnit, GeoRadiusParam geoRadiusParam) {
        return null;
    }

    @Override
    public List<Long> bitfield(String s, String... strings) {
        return null;
    }

    @Override
    public Long del(final String... keys) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.del(keys);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long exists(String... strings) {
        return null;
    }

    @Override
    public List<String> blpop(final int timeout, final String... keys) {
        try {
            return new JedisExecute<List<String>>(pool) {
                @Override
                public List<String> execute(Jedis connection) throws RedisException {
                    return connection.blpop(timeout, keys);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> brpop(final int timeout, final String... keys) {
        try {
            return new JedisExecute<List<String>>(pool) {
                @Override
                public List<String> execute(Jedis connection) throws RedisException {
                    return connection.brpop(timeout, keys);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> blpop(final String... args) {
        try {
            return new JedisExecute<List<String>>(pool) {
                @Override
                public List<String> execute(Jedis connection) throws RedisException {
                    return connection.blpop(args);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> brpop(final String... args) {
        try {
            return new JedisExecute<List<String>>(pool) {
                @Override
                public List<String> execute(Jedis connection) throws RedisException {
                    return connection.brpop(args);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> keys(final String pattern) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.keys(pattern);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> mget(final String... keys) {
        try {
            return new JedisExecute<List<String>>(pool) {
                @Override
                public List<String> execute(Jedis connection) throws RedisException {
                    return connection.mget(keys);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String mset(final String... keysvalues) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.mset(keysvalues);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long msetnx(final String... keysvalues) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.msetnx(keysvalues);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String rename(final String oldkey, final String newkey) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.rename(oldkey, newkey);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long renamenx(final String oldkey, final String newkey) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.renamenx(oldkey, newkey);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String rpoplpush(final String srckey, final String dstkey) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.rpoplpush(srckey, dstkey);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> sdiff(final String... keys) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.sdiff(keys);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long sdiffstore(final String dstkey, final String... keys) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.sdiffstore(dstkey, keys);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> sinter(final String... keys) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.sinter(keys);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long sinterstore(final String dstkey, final String... keys) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.sinterstore(dstkey, keys);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long smove(final String srckey, final String dstkey, final String member) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.smove(srckey, dstkey, member);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long sort(final String key, final SortingParams sortingParameters, final String dstkey) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.sort(key, sortingParameters, dstkey);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long sort(final String key, final String dstkey) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.sort(key, dstkey);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> sunion(final String... keys) {
        try {
            return new JedisExecute<Set<String>>(pool) {
                @Override
                public Set<String> execute(Jedis connection) throws RedisException {
                    return connection.sunion(keys);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long sunionstore(final String dstkey, final String... keys) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.sunionstore(dstkey, keys);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String watch(final String... keys) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.watch(keys);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String unwatch() {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.unwatch();
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long zinterstore(final String dstkey, final String... sets) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zinterstore(dstkey, sets);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long zinterstore(final String dstkey, final ZParams params, final String... sets) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zinterstore(dstkey, params, sets);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long zunionstore(final String dstkey, final String... sets) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zunionstore(dstkey, sets);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long zunionstore(final String dstkey, final ZParams params, final String... sets) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.zunionstore(dstkey, params, sets);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @Override
    public String brpoplpush(final String source, final String destination, final int timeout) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.brpoplpush(source, destination, timeout);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long publish(final String channel, final String message) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.publish(channel, message);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @Override
    public void subscribe(final JedisPubSub jedisPubSub, final String... channels) {
        try {
            new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    connection.subscribe(jedisPubSub, channels);
                    return 0L;
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void psubscribe(final JedisPubSub jedisPubSub, final String... patterns) {
        try {
            new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    connection.subscribe(jedisPubSub, patterns);
                    return 0L;
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String randomKey() {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.randomKey();
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long bitop(final BitOP op, final String destKey, final String... srcKeys) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.bitop(op, destKey, srcKeys);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @Override
    public ScanResult<String> scan(final int cursor) {
        try {
            return new JedisExecute<ScanResult<String>>(pool) {
                @Override
                public ScanResult<String> execute(Jedis connection) throws RedisException {
                    return connection.scan(cursor);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ScanResult<String> scan(final String cursor) {
        try {
            return new JedisExecute<ScanResult<String>>(pool) {
                @Override
                public ScanResult<String> execute(Jedis connection) throws RedisException {
                    return connection.scan(cursor);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ScanResult<String> scan(String s, ScanParams scanParams) {
        return null;
    }

    @Override
    public String pfmerge(final String destkey, final String... sourcekeys) {
        try {
            return new JedisExecute<String>(pool) {
                @Override
                public String execute(Jedis connection) throws RedisException {
                    return connection.pfmerge(destkey, sourcekeys);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long pfcount(final String... keys) {
        try {
            return new JedisExecute<Long>(pool) {
                @Override
                public Long execute(Jedis connection) throws RedisException {
                    return connection.pfcount(keys);
                }
            }.run();
        } catch (RedisException e) {
            e.printStackTrace();
        }
        return 0L;
    }
}
