package com.musinsa.point.service;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LockService {

    private final RedisTemplate<String, String> redisTemplate;

    public LockService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean acquire(String key) {
        return acquire(key, 10);
    }

    public boolean acquire(String key, long expireSeconds) {
        try {
            var result = redisTemplate.opsForValue().setIfAbsent(key, "0", expireSeconds, TimeUnit.SECONDS);
            return result != null && result;
        } catch (Exception e) {
            return true;
        }
    }
}