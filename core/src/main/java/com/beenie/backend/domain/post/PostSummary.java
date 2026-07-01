package com.beenie.backend.domain.post;

import java.time.LocalDateTime;
import java.util.List;

public record PostSummary(
        Long id,
        String title,
        String slug,
        String summary,
        String thumbnailUrl,
        PostStatus status,
        long viewCount,
        long likeCount,
        long bookmarkCount,
        Long categoryId,
        String categoryName,
        String categorySlug,
        List<String> tags,
        String highlight,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
