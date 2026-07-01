package com.beenie.backend.web.category;

import com.beenie.backend.domain.category.CategoryCount;

public record CategoryResponse(Long id, String name, String slug, long postCount) {
    public static CategoryResponse from(CategoryCount count) {
        return new CategoryResponse(count.id(), count.name(), count.slug(), count.postCount());
    }
}
