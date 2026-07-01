package com.beenie.backend.infrastructure.persistence;

import com.beenie.backend.domain.activity.PostLikeRepository;
import com.beenie.backend.storage.jpa.entity.PostLikeJpaEntity;
import com.beenie.backend.storage.jpa.repository.PostLikeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostLikeRepositoryAdapter implements PostLikeRepository {

    private final PostLikeJpaRepository postLikeJpaRepository;

    @Override
    public boolean exists(Long userId, Long postId) {
        return postLikeJpaRepository.existsById(new PostLikeJpaEntity.PostLikeId(userId, postId));
    }

    @Override
    public void add(Long userId, Long postId) {
        if (!exists(userId, postId)) {
            postLikeJpaRepository.save(new PostLikeJpaEntity(userId, postId));
        }
    }

    @Override
    public void remove(Long userId, Long postId) {
        postLikeJpaRepository.deleteById(new PostLikeJpaEntity.PostLikeId(userId, postId));
    }
}
