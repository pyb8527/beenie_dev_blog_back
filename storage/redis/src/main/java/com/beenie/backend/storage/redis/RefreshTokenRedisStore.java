package com.beenie.backend.storage.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * Refresh Token 저장을 위한 Redis 접근 컴포넌트. key: refresh:{userId}
 */
@Component
@RequiredArgsConstructor
public class RefreshTokenRedisStore {

    private static final String KEY_PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;

    public void save(Long userId, String token, long ttlSeconds) {
        redisTemplate.opsForValue().set(key(userId), token, Duration.ofSeconds(ttlSeconds));
    }

    public Optional<String> find(Long userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key(userId)));
    }

    public void delete(Long userId) {
        redisTemplate.delete(key(userId));
    }

    private String key(Long userId) {
        return KEY_PREFIX + userId;
    }
}
