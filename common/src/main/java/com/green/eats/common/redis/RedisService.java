package com.green.eats.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

//Redis 사용 용도 (CRUD)

@Service
@RequiredArgsConstructor
@ConditionalOnClass(RedisTemplate.class)
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 1. Create & Update (Redis는 Key가 같으면 덮어쓰므로 동일함)
    public void save(String key, Object value, long timeoutSeconds) {
        // 데이터 저장 및 만료 시간 설정
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeoutSeconds));
    }

    // 2. Read
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 3. Delete
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    // 4. Check Existence
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}