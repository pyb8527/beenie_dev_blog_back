package com.beenie.backend.infrastructure.persistence;

import com.beenie.backend.domain.category.Category;
import com.beenie.backend.domain.category.CategoryCount;
import com.beenie.backend.domain.category.CategoryRepository;
import com.beenie.backend.storage.jpa.entity.CategoryJpaEntity;
import com.beenie.backend.storage.jpa.repository.CategoryJpaRepository;
import com.beenie.backend.storage.jpa.repository.CategoryPostCountRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public List<Category> findAll() {
        return categoryJpaRepository.findAll().stream().map(CategoryRepositoryAdapter::toDomain).toList();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryJpaRepository.findById(id).map(CategoryRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return categoryJpaRepository.findByName(name).map(CategoryRepositoryAdapter::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryJpaRepository.existsByName(name);
    }

    @Override
    public Category save(Category category) {
        CategoryJpaEntity entity = CategoryJpaEntity.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .build();
        return toDomain(categoryJpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        categoryJpaRepository.deleteById(id);
    }

    @Override
    public List<CategoryCount> findAllWithPostCount() {
        return categoryJpaRepository.findAllWithPostCount().stream()
                .map(CategoryRepositoryAdapter::toCount)
                .toList();
    }

    private static Category toDomain(CategoryJpaEntity entity) {
        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private static CategoryCount toCount(CategoryPostCountRow row) {
        return new CategoryCount(row.id(), row.name(), row.slug(), row.postCount());
    }
}
