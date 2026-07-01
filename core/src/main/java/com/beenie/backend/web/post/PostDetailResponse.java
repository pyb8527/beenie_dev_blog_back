package com.beenie.backend.web.post;

import com.beenie.backend.application.post.PostDetailResult;
import com.beenie.backend.domain.post.Post;
import com.beenie.backend.domain.post.PostNavItem;
import com.beenie.backend.infrastructure.markdown.TocItem;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse(
        Long id,
        String title,
        String slug,
        String content,
        String thumbnailUrl,
        String status,
        long viewCount,
        long likeCount,
        long bookmarkCount,
        Long categoryId,
        String categoryName,
        String categorySlug,
        List<String> tags,
        List<TocItem> toc,
        PostNavItem prevPost,
        PostNavItem nextPost,
        boolean liked,
        boolean bookmarked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PostDetailResponse from(PostDetailResult result) {
        Post post = result.post();
        return new PostDetailResponse(
                post.getId(), post.getTitle(), post.getSlug(), post.getHtmlContent(), post.getThumbnailUrl(),
                post.getStatus().name(), post.getViewCount(), post.getLikeCount(), post.getBookmarkCount(),
                post.getCategoryId(),
                result.category() != null ? result.category().getName() : "미분류",
                result.category() != null ? result.category().getSlug() : "uncategorized",
                post.getTags(), result.toc(), result.previous(), result.next(),
                result.liked(), result.bookmarked(),
                post.getCreatedAt(), post.getUpdatedAt());
    }
}
