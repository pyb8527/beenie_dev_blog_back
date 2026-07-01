package com.beenie.backend.storage.jpa.repository;

import com.beenie.backend.storage.jpa.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Long> {

    Optional<CategoryJpaEntity> findByName(String name);

    boolean existsByName(String name);

    @Query("""
            SELECT new com.beenie.backend.storage.jpa.repository.CategoryPostCountRow(
                c.id, c.name, c.slug, COUNT(p.id))
            FROM CategoryJpaEntity c
            LEFT JOIN PostJpaEntity p ON p.categoryId = c.id AND p.status = 'PUBLIC' AND p.deletedAt IS NULL
            GROUP BY c.id, c.name, c.slug
            ORDER BY c.id ASC
            """)
    List<CategoryPostCountRow> findAllWithPostCount();
}
