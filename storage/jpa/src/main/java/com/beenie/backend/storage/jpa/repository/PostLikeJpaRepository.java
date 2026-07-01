package com.beenie.backend.storage.jpa.repository;

import com.beenie.backend.storage.jpa.entity.PostLikeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeJpaRepository extends JpaRepository<PostLikeJpaEntity, PostLikeJpaEntity.PostLikeId> {
}
