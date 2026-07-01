package com.beenie.backend.web.comment;

import com.beenie.backend.domain.comment.Comment;

import java.time.LocalDateTime;

public record AdminCommentResponse(
        Long id,
        Long postId,
        String postTitle,
        String authorName,
        Long parentId,
        String content,
        boolean isDeleted,
        LocalDateTime createdAt
) {
    public static AdminCommentResponse from(Comment comment, String postTitle) {
        return new AdminCommentResponse(comment.getId(), comment.getPostId(), postTitle, comment.getAuthorName(),
                comment.getParentId(), comment.getContent(), comment.isDeleted(), comment.getCreatedAt());
    }
}
