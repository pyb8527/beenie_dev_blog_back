package com.beenie.backend.infrastructure.persistence;

import com.beenie.backend.domain.activity.PostBookmarkRepository;
import com.beenie.backend.storage.jpa.entity.PostBookmarkJpaEntity;
import com.beenie.backend.storage.jpa.repository.PostBookmarkJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostBookmarkRepositoryAdapter implements PostBookmarkRepository {

    private final PostBookmarkJpaRepository postBookmarkJpaRepository;

    @Override
    public boolean exists(Long userId, Long postId) {
        return postBookmarkJpaRepository.existsById(new PostBookmarkJpaEntity.PostBookmarkId(userId, postId));
    }

    @Override
    public void add(Long userId, Long postId) {
        if (!exists(userId, postId)) {
            postBookmarkJpaRepository.save(new PostBookmarkJpaEntity(userId, postId));
        }
    }

    @Override
    public void remove(Long userId, Long postId) {
        postBookmarkJpaRepository.deleteById(new PostBookmarkJpaEntity.PostBookmarkId(userId, postId));
    }
}
