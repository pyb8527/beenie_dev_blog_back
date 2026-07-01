package com.beenie.backend.web.category;

import com.beenie.backend.domain.category.Category;

public record CategoryDetailResponse(Long id, String name, String slug) {
    public static CategoryDetailResponse from(Category category) {
        return new CategoryDetailResponse(category.getId(), category.getName(), category.getSlug());
    }
}
