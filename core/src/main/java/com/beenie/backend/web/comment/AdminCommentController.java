package com.beenie.backend.web.comment;

import com.beenie.backend.application.comment.CommentService;
import com.beenie.backend.infrastructure.security.AuthPrincipal;
import com.beenie.backend.support.common.response.ApiResponse;
import com.beenie.backend.support.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    public ApiResponse<PageResponse<AdminCommentResponse>> list(
            @RequestParam(required = false) Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var comments = commentService.listForAdmin(postId, page, size);
        long total = commentService.countForAdmin(postId);
        var titles = commentService.resolvePostTitles(comments);
        var mapped = comments.stream()
                .map(c -> AdminCommentResponse.from(c, titles.get(c.getPostId())))
                .toList();
        return ApiResponse.success(PageResponse.of(mapped, page, size, total));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, @AuthenticationPrincipal AuthPrincipal principal) {
        commentService.delete(id, principal.userId(), true);
        return ApiResponse.empty();
    }
}
