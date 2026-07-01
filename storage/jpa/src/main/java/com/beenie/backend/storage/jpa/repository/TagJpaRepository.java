package com.beenie.backend.storage.jpa.repository;

import com.beenie.backend.storage.jpa.entity.TagJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TagJpaRepository extends JpaRepository<TagJpaEntity, Long> {

    Optional<TagJpaEntity> findByName(String name);

    List<TagJpaEntity> findAllByNameIn(Collection<String> names);

    @Query("""
            SELECT new com.beenie.backend.storage.jpa.repository.TagUsageCountRow(t.id, t.name, COUNT(pt.id.postId))
            FROM TagJpaEntity t
            LEFT JOIN PostTagJpaEntity pt ON pt.id.tagId = t.id
            GROUP BY t.id, t.name
            ORDER BY t.id ASC
            """)
    List<TagUsageCountRow> findAllWithUsageCount();

    @Query("SELECT t.name FROM TagJpaEntity t JOIN PostTagJpaEntity pt ON pt.id.tagId = t.id WHERE pt.id.postId = :postId")
    List<String> findNamesByPostId(@org.springframework.data.repository.query.Param("postId") Long postId);
}
