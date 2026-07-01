package com.beenie.backend.web.post;

import com.beenie.backend.domain.post.PostSummary;

import java.time.LocalDateTime;
import java.util.List;

public record PostSummaryResponse(
        Long id,
        String title,
        String slug,
        String summary,
        String thumbnailUrl,
        String status,
        long viewCount,
        long likeCount,
        long bookmarkCount,
        String categoryName,
        String categorySlug,
        List<String> tags,
        String highlight,
        LocalDateTime createdAt
) {
    public static PostSummaryResponse from(PostSummary summary) {
        return new PostSummaryResponse(
                summary.id(), summary.title(), summary.slug(), summary.summary(), summary.thumbnailUrl(),
                summary.status().name(), summary.viewCount(), summary.likeCount(), summary.bookmarkCount(),
                summary.categoryName(), summary.categorySlug(), summary.tags(), summary.highlight(), summary.createdAt());
    }
}
