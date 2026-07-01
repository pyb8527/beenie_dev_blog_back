package com.beenie.backend.storage.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 방문자 수 집계를 위한 Redis HyperLogLog 컴포넌트. key: visitors:{yyyyMMdd}
 */
@Component
@RequiredArgsConstructor
public class VisitorStatsRedisStore {

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String KEY_PREFIX = "visitors:";
    private static final Duration TTL = Duration.ofDays(60);

    private final StringRedisTemplate redisTemplate;

    public void recordVisit(String clientKey, LocalDate date) {
        String key = dayKey(date);
        redisTemplate.opsForHyperLogLog().add(key, clientKey);
        redisTemplate.expire(key, TTL);
    }

    public long countVisitors(LocalDate date) {
        Long count = redisTemplate.opsForHyperLogLog().size(dayKey(date));
        return count == null ? 0L : count;
    }

    public long countVisitors(LocalDate from, LocalDate to) {
        java.util.List<String> keys = new java.util.ArrayList<>();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            keys.add(dayKey(cursor));
            cursor = cursor.plusDays(1);
        }
        if (keys.isEmpty()) {
            return 0L;
        }
        String mergeKey = "visitors:merge:" + System.nanoTime();
        try {
            Long merged = redisTemplate.opsForHyperLogLog().union(mergeKey, keys.toArray(new String[0]));
            return merged == null ? 0L : merged;
        } finally {
            redisTemplate.delete(mergeKey);
        }
    }

    private String dayKey(LocalDate date) {
        return KEY_PREFIX + date.format(DAY);
    }
}
