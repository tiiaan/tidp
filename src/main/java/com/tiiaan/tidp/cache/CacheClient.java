package com.tiiaan.tidp.cache;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tiiaan.tidp.entity.Shop;
import com.tiiaan.tidp.utils.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.swing.text.html.ObjectView;
import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.function.Function;


/**
 * @author tiiaan Email:tiiaan.w@gmail.com
 * @version 0.0
 * Redis 缓存工具类，解决缓存穿透、缓存击穿、缓存雪崩问题
 */


@Component
public class CacheClient {

    private final StringRedisTemplate stringRedisTemplate;

    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE_SIZE = 15;
    private static final long KEEP_ALIVE_TIME = 1L;
    private static final int BLOCKING_QUEUE_CAPACITY = 5;
    private static final ExecutorService CACHE_REBUILD_POOL = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(BLOCKING_QUEUE_CAPACITY),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    private static final String LOCK_KEY = "lock:";
    private static final Long LOCK_TTL = 10L;
    private static final Long NULL_TTL = 2L;


    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }




    /**
     * 写缓存，设置过期时间
     * @param key 缓存key
     * @param value 值
     * @param time 有效期
     * @param timeUnit 有效期时间单位
     * @author tiiaan Email:tiiaan.w@gmail.com
     */
    public void set(String key, Object value, Long time, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, timeUnit);
    }




    /**
     * 写缓存，设置逻辑过期时间
     * @param key 缓存key
     * @param value 值
     * @param time 逻辑有效期
     * @param timeUnit 逻辑有效期时间单位
     * @author tiiaan Email:tiiaan.w@gmail.com
     */
    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit timeUnit) {
        LogicalExpireData logicalExpireData = new LogicalExpireData();
        logicalExpireData.setData(value);
        logicalExpireData.setExpireTime(LocalDateTime.now().plusSeconds(timeUnit.toSeconds(time)));
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(logicalExpireData));
    }




    /**
     * 查询缓存，使用缓存空值解决缓存穿透问题
     * @param prefix key前缀
     * @param queryKey 查询字段
     * @param clazz 查询返回值类型
     * @param dbFallback 数据库查询逻辑
     * @param time 缓存有效期
     * @param timeUnit 缓存有效期时间单位
     * @return T
     * @author tiiaan Email:tiiaan.w@gmail.com
     */
    public <T, R> T queryWithPassThrough(String prefix, R queryKey, Class<T> clazz, Function<R, T> dbFallback, Long time, TimeUnit timeUnit) {
        String key = prefix + queryKey;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null && json.length() != 0) {
            return JSONUtil.toBean(json, clazz);
        }
        if (json != null) {
            return null;
        }
        T value = dbFallback.apply(queryKey);
        if (value == null) {
            stringRedisTemplate.opsForValue().set(key, "", NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        this.set(key, value, time, timeUnit);
        return value;
    }




    /**
     * 查询缓存，使用互斥锁解决缓存击穿问题
     * @param prefix key前缀
     * @param queryKey 查询字段
     * @param clazz 查询返回值类型
     * @param dbFallback 数据库查询逻辑
     * @param time 缓存有效期
     * @param timeUnit 缓存有效期时间单位
     * @return T
     * @author tiiaan Email:tiiaan.w@gmail.com
     */
    public <T, R> T queryWithMutex(String prefix, R queryKey, Class<T> clazz, Function<R, T> dbFallback, Long time, TimeUnit timeUnit) {
        String key = prefix + queryKey;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null && json.length() != 0) {
            return JSONUtil.toBean(json, clazz);
        }
        if (json != null) {
            return null;
        }
        T value;
        boolean isLock = this.tryLock(key);
        try {
            if (!isLock) {
                try { TimeUnit.MILLISECONDS.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
                return this.queryWithMutex(prefix, queryKey, clazz, dbFallback, time, timeUnit);
            }
            value = dbFallback.apply(queryKey);
            if (value == null) {
                stringRedisTemplate.opsForValue().set(key, "", NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            this.set(key, value, time, timeUnit);
        } finally {
            this.tryUnlock(key);
        }
        return value;
    }




    /**
     * 查询缓存，使用逻辑过期解决缓存击穿问题
     * @param prefix key前缀
     * @param queryKey 查询字段
     * @param clazz 查询返回值类型
     * @param dbFallback 数据库查询逻辑
     * @param time 缓存有效期
     * @param timeUnit 缓存有效期时间单位
     * @return T
     * @author tiiaan Email:tiiaan.w@gmail.com
     */
    public <T, R> T queryWithLogicalExpire(String prefix, R queryKey, Class<T> clazz, Function<R, T> dbFallback, Long time, TimeUnit timeUnit) {
        String key = prefix + queryKey;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null || json.length() != 0) {
            return null;
        }
        LogicalExpireData logicalExpireData = JSONUtil.toBean(json, LogicalExpireData.class);
        T value = clazz.cast(logicalExpireData.getData());
        LocalDateTime expireTime = logicalExpireData.getExpireTime();
        if (expireTime.isAfter(LocalDateTime.now())) {
            return value;
        }
        boolean isLock = this.tryLock(key);
        if (isLock) {
            CACHE_REBUILD_POOL.submit(() -> {
                try {
                    T newValue = dbFallback.apply(queryKey);
                    this.setWithLogicalExpire(key, newValue, time, timeUnit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    this.tryUnlock(key);
                }
            });
        }
        return value;
    }



    private boolean tryLock(String lock) {
        Boolean isLock = stringRedisTemplate.opsForValue().setIfAbsent(lock + LOCK_KEY, "1", LOCK_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(isLock);
    }


    private void tryUnlock(String lock) {
        stringRedisTemplate.delete(lock + LOCK_KEY);
    }


    private class LogicalExpireData {
        private LocalDateTime expireTime;
        private Object data;

        public LocalDateTime getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(LocalDateTime expireTime) {
            this.expireTime = expireTime;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }


}
