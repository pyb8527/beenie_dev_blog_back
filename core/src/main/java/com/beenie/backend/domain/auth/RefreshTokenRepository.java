package com.beenie.backend.domain.auth;

import java.util.Optional;

/**
 * Refresh Token 저장소 Port (Redis 구현, key pattern: refresh:{userId}).
 * RTR(Rotation) 전략과 재사용 탐지를 지원하기 위해 사용자당 하나의 현재 유효 토큰만 보관한다.
 */
public interface RefreshTokenRepository {

    void save(Long userId, String token, long ttlSeconds);

    Optional<String> find(Long userId);

    void delete(Long userId);
}
