package com.tiiaan.tidp.cache;

import cn.hutool.core.lang.UUID;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author tiiaan Email:tiiaan.w@gmail.com
 * @version 0.0
 * description
 */

public class SimpleRedisLock {

    private final String key;
    private final String value;
    private StringRedisTemplate stringRedisTemplate;


    private static final String LOCK_KEY_PREFIX = "lock:";
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;
    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }


    public SimpleRedisLock(String suffix, StringRedisTemplate stringRedisTemplate) {
        key = LOCK_KEY_PREFIX + suffix;
        value = UUID.randomUUID().toString(true);
        this.stringRedisTemplate = stringRedisTemplate;
    }


    public boolean tryLock(long timeout, TimeUnit timeunit) {
        Boolean isLock = stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, timeunit);
        return Boolean.TRUE.equals(isLock);
    }


    public void unlock() {
        stringRedisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(key), value);
    }

}
