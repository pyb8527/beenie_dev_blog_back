package com.beenie.backend.storage.jpa.repository;

import com.beenie.backend.storage.jpa.entity.PostJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<PostJpaEntity, Long> {

    Optional<PostJpaEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Modifying
    @Query("UPDATE PostJpaEntity p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE PostJpaEntity p SET p.likeCount = p.likeCount + :delta WHERE p.id = :id")
    void changeLikeCount(@Param("id") Long id, @Param("delta") int delta);

    @Modifying
    @Query("UPDATE PostJpaEntity p SET p.bookmarkCount = p.bookmarkCount + :delta WHERE p.id = :id")
    void changeBookmarkCount(@Param("id") Long id, @Param("delta") int delta);

    @Modifying
    @Query("UPDATE PostJpaEntity p SET p.deletedAt = :deletedAt WHERE p.id = :id")
    void softDelete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);

    @Modifying
    @Query("UPDATE PostJpaEntity p SET p.categoryId = 1 WHERE p.categoryId = :categoryId")
    void reassignCategoryToDefault(@Param("categoryId") Long categoryId);
}
