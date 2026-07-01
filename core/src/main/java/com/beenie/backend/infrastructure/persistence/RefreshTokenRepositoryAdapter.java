package com.beenie.backend.infrastructure.persistence;

import com.beenie.backend.domain.auth.RefreshTokenRepository;
import com.beenie.backend.storage.redis.RefreshTokenRedisStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final RefreshTokenRedisStore store;

    @Override
    public void save(Long userId, String token, long ttlSeconds) {
        store.save(userId, token, ttlSeconds);
    }

    @Override
    public Optional<String> find(Long userId) {
        return store.find(userId);
    }

    @Override
    public void delete(Long userId) {
        store.delete(userId);
    }
}
