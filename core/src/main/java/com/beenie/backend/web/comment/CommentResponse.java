package com.beenie.backend.web.comment;

import com.beenie.backend.domain.comment.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record CommentResponse(
        Long id,
        String authorName,
        String authorAvatarUrl,
        String content,
        boolean isDeleted,
        LocalDateTime createdAt,
        List<CommentResponse> replies
) {

    private static final String DELETED_PLACEHOLDER = "이 댓글은 삭제되었습니다.";

    /** 게시글의 평면(flat) 댓글 목록을 2단계 트리로 변환한다. 대댓글이 없는 삭제된 댓글은 완전히 제외한다. */
    public static List<CommentResponse> buildTree(List<Comment> flat) {
        Map<Long, List<Comment>> repliesByParent = new LinkedHashMap<>();
        List<Comment> topLevel = new ArrayList<>();

        for (Comment comment : flat) {
            if (comment.isTopLevel()) {
                topLevel.add(comment);
            } else {
                repliesByParent.computeIfAbsent(comment.getParentId(), k -> new ArrayList<>()).add(comment);
            }
        }

        List<CommentResponse> result = new ArrayList<>();
        for (Comment top : topLevel) {
            List<Comment> replies = repliesByParent.getOrDefault(top.getId(), List.of());
            if (top.isDeleted() && replies.isEmpty()) {
                continue;
            }
            List<CommentResponse> replyResponses = replies.stream().map(CommentResponse::fromLeaf).toList();
            result.add(fromTop(top, replyResponses));
        }
        return result;
    }

    private static CommentResponse fromTop(Comment comment, List<CommentResponse> replies) {
        return new CommentResponse(
                comment.getId(),
                comment.isDeleted() ? null : comment.getAuthorName(),
                comment.isDeleted() ? null : comment.getAuthorAvatarUrl(),
                comment.isDeleted() ? DELETED_PLACEHOLDER : comment.getContent(),
                comment.isDeleted(),
                comment.getCreatedAt(),
                replies);
    }

    public static CommentResponse fromLeaf(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.isDeleted() ? null : comment.getAuthorName(),
                comment.isDeleted() ? null : comment.getAuthorAvatarUrl(),
                comment.isDeleted() ? DELETED_PLACEHOLDER : comment.getContent(),
                comment.isDeleted(),
                comment.getCreatedAt(),
                List.of());
    }
}
