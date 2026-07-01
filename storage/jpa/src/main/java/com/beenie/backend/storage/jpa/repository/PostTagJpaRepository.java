package com.beenie.backend.storage.jpa.repository;

import com.beenie.backend.storage.jpa.entity.PostTagJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostTagJpaRepository extends JpaRepository<PostTagJpaEntity, PostTagJpaEntity.PostTagId> {

    @Query("SELECT pt FROM PostTagJpaEntity pt WHERE pt.id.postId = :postId")
    List<PostTagJpaEntity> findAllByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM PostTagJpaEntity pt WHERE pt.id.postId = :postId")
    void deleteAllByPostId(@Param("postId") Long postId);
}
