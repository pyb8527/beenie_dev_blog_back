package com.beenie.backend.domain.category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    List<Category> findAll();

    Optional<Category> findById(Long id);

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    Category save(Category category);

    void deleteById(Long id);

    List<CategoryCount> findAllWithPostCount();
}
