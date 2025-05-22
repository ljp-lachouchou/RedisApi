package com.software.redisapi;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisFactory {
    private static final JedisPool jedisPool;
    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(100);
        config.setMinIdle(0);
        jedisPool = new JedisPool(
                config,
                "127.0.0.1",
                6379,
                5000
                );

    }////
    public static Jedis getRedis(){
        return jedisPool.getResource();
    }
}
