package com.beenie.backend.domain.post;

/** 조회수 중복 방지 Port (Redis SETNX 구현, key: view:{postId}:{ip}, TTL 24h). */
public interface ViewCountDedupRepository {

    boolean markViewedIfAbsent(Long postId, String ip);
}
