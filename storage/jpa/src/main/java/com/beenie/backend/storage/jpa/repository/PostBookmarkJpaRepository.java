package com.beenie.backend.storage.jpa.repository;

import com.beenie.backend.storage.jpa.entity.PostBookmarkJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostBookmarkJpaRepository extends JpaRepository<PostBookmarkJpaEntity, PostBookmarkJpaEntity.PostBookmarkId> {
}
