package com.beenie.backend.storage.jpa.repository;

import com.beenie.backend.storage.jpa.entity.CommentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentJpaRepository extends JpaRepository<CommentJpaEntity, Long> {

    List<CommentJpaEntity> findAllByPostIdOrderByCreatedAtAsc(Long postId);

    long countByParentId(Long parentId);

    @Modifying
    @Query("UPDATE CommentJpaEntity c SET c.deleted = true, c.deletedAt = :deletedAt WHERE c.id = :id")
    void softDelete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);

    @Modifying
    @Query("UPDATE CommentJpaEntity c SET c.deleted = true, c.deletedAt = :deletedAt WHERE c.postId = :postId")
    void softDeleteAllByPostId(@Param("postId") Long postId, @Param("deletedAt") LocalDateTime deletedAt);

    @Query("SELECT c FROM CommentJpaEntity c WHERE (:postId IS NULL OR c.postId = :postId) ORDER BY c.createdAt DESC")
    List<CommentJpaEntity> findAllForAdmin(@Param("postId") Long postId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT COUNT(c) FROM CommentJpaEntity c WHERE (:postId IS NULL OR c.postId = :postId)")
    long countForAdmin(@Param("postId") Long postId);
}
