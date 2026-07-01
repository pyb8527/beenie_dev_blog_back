package com.beenie.backend.storage.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

/**
 * 게시글 목록 첫 페이지 캐시 (JSON 문자열 저장). 게시글 생성/수정/삭제 시 evictAll() 로 무효화한다.
 */
@Component
@RequiredArgsConstructor
public class PostListCacheStore {

    private static final String KEY_PREFIX = "post-list:";
    private static final Duration TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;

    public void put(String cacheKey, String json) {
        redisTemplate.opsForValue().set(KEY_PREFIX + cacheKey, json, TTL);
    }

    public Optional<String> get(String cacheKey) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(KEY_PREFIX + cacheKey));
    }

    public void evictAll() {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
