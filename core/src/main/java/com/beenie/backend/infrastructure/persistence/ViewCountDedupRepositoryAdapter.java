package com.beenie.backend.infrastructure.persistence;

import com.beenie.backend.domain.post.ViewCountDedupRepository;
import com.beenie.backend.storage.redis.ViewCountRedisStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewCountDedupRepositoryAdapter implements ViewCountDedupRepository {

    private static final long TTL_SECONDS = 24 * 60 * 60L;

    private final ViewCountRedisStore store;

    @Override
    public boolean markViewedIfAbsent(Long postId, String ip) {
        return store.markViewedIfAbsent(postId, ip, TTL_SECONDS);
    }
}
