package com.beenie.backend.web.post;

import com.beenie.backend.domain.post.Post;

import java.time.LocalDateTime;
import java.util.List;

public record AdminPostResponse(
        Long id,
        String title,
        String slug,
        String content,
        String thumbnailUrl,
        String status,
        long viewCount,
        long likeCount,
        Long categoryId,
        List<String> tags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AdminPostResponse from(Post post) {
        return new AdminPostResponse(
                post.getId(), post.getTitle(), post.getSlug(), post.getContent(), post.getThumbnailUrl(),
                post.getStatus().name(), post.getViewCount(), post.getLikeCount(), post.getCategoryId(),
                post.getTags(), post.getCreatedAt(), post.getUpdatedAt());
    }
}
