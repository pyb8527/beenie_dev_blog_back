package com.beenie.backend.storage.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 게시글 조회수 중복 방지를 위한 Redis SETNX 컴포넌트. key: view:{postId}:{ip}, TTL 24h
 */
@Component
@RequiredArgsConstructor
public class ViewCountRedisStore {

    private final StringRedisTemplate redisTemplate;

    /**
     * @return true 이면 신규 조회(새로 SETNX 성공), false 이면 24시간 내 중복 조회
     */
    public boolean markViewedIfAbsent(Long postId, String ip, long ttlSeconds) {
        String key = "view:" + postId + ":" + ip;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(ttlSeconds));
        return Boolean.TRUE.equals(success);
    }
}
