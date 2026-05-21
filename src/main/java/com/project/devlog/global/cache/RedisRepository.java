package com.project.devlog.global.cache;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final StringRedisTemplate stringRedisTemplate;

    public void save(String key, String value, int TTL, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, TTL, timeUnit);
    }

    public String findByKey(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void delete(String refreshKey) {
        stringRedisTemplate.delete(refreshKey);
    }
}
