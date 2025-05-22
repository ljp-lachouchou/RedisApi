package com.software.redisapi;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;

public class RedisService {
    private static final Gson GSON = new Gson();
    public static void setEx(String key,String value,int ex){
        try(Jedis jedis = RedisFactory.getRedis()){
            jedis.setex(key,ex,value);
        }
    }
    // 存储对象（JSON 序列化）
    public static <T> void setObject(String key, T obj, int expireSeconds) {
        try (Jedis jedis = RedisFactory.getRedis()) {
            String json = GSON.toJson(obj);
            jedis.setex(key, expireSeconds, json);
        }
    }

    // 获取对象（反序列化）
    public static <T> T getObject(String key, Class<T> clazz) {
        try (Jedis jedis = RedisFactory.getRedis()) {
            String json = jedis.get(key);
            return GSON.fromJson(json, clazz);
        }
    }
    public static void set(String key,String value){
        try(Jedis jedis = RedisFactory.getRedis()){
            jedis.set(key,value);
        }
    }
    public static String get(String key){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.get(key);
        }
    }
    public static Long del(String... keys){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.del(keys);
        }
    }////
    public static void hSet(String key, Map<String,String> hash){
        try(Jedis jedis = RedisFactory.getRedis()){
            jedis.hset(key, hash);
        }
    }
    public static Long sRem(String key,String member){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.srem(key,member);
        }

    }
    public static String hGet(String key, String field){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.hget(key,field);
        }
    }
    public static Long hLen(String key){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.hlen(key);
        }
    }
    public static Long hDel(String key,String... field){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.hdel(key,field);
        }
    }
    public static Long lLen(String key){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.llen(key);
        }
    }
    public static List<String> lRange(String key , Long start, Long end){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.lrange(key,start,end);
        }
    }
    public static Long lPush(String key , String... value){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.lpush(key,value);
        }
    }
    public static String lPop(String key){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.lpop(key);
        }
    }
    public static String rPop(String key){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.rpop(key);
        }
    }
    public static Long rPush(String key , String... value){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.rpush(key,value);
        }
    }
    public static Long sAdd(String key,String... value){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.sadd(key,value);
        }
    }
    public static Set<String> sMembers(String key){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.smembers(key);
        }
    }
    public static boolean sIsMembers(String key,String value){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.sismember(key, value);
        }
    }
    public static Long sCard(String key){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.scard(key);
        }
    }
    //默认降序
    public static Long zAdd(String key,Map<String,Double> scoreMembers){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.zadd(key,scoreMembers);
        }
    }
    public static Long zCard(String key){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.zcard(key);
        }
    }
    //从大开始删除
    public static Set<Tuple> zPopMax(String key, Integer count){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.zpopmax(key, count);
        }
    }

    public static Set<Tuple> zPopMin(String key, Integer count){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.zpopmin(key, count);
        }
    }
    public static Long zRank(String key,String member){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.zrank(key,member);
        }
    }
    public static Double zScore(String key,String member){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.zscore(key,member);
        }
    }
    public static Set<String> zRangeByScore(String key, Integer min, Integer max){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.zrangeByScore(key,min,max);
        }
    }
    public static Set<String> zRange(String key, Integer start, Integer stop){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.zrange(key,start,stop);
        }
    }

    public static Long zRevRank(String key,String member){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.zrevrank(key,member);
        }
    }
    //降序排列
    public static Set<String> zRevRange(String key, Integer start, Integer stop){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.zrevrange(key,start,stop);
        }
    }
    public static Double zIncrBy(String key,Double value,String member){
        try(Jedis jedis = RedisFactory.getRedis()){
            return jedis.zincrby(key,value,member);
        }
    }



}
