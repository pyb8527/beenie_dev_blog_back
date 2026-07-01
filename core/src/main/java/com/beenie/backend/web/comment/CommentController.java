package com.beenie.backend.web.comment;

import com.beenie.backend.application.comment.CommentService;
import com.beenie.backend.domain.comment.Comment;
import com.beenie.backend.infrastructure.security.AuthPrincipal;
import com.beenie.backend.support.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/posts/{postId}/comments")
    public ApiResponse<List<CommentResponse>> list(@PathVariable Long postId) {
        List<Comment> comments = commentService.listByPost(postId);
        return ApiResponse.success(CommentResponse.buildTree(comments));
    }

    @PostMapping("/api/posts/{postId}/comments")
    public ApiResponse<Void> create(@PathVariable Long postId,
                                     @Valid @RequestBody CommentCreateRequest request,
                                     @AuthenticationPrincipal AuthPrincipal principal) {
        commentService.create(postId, principal.userId(), request.content(), request.parentId());
        return ApiResponse.empty();
    }

    @PutMapping("/api/comments/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                     @Valid @RequestBody CommentUpdateRequest request,
                                     @AuthenticationPrincipal AuthPrincipal principal) {
        commentService.update(id, principal.userId(), principal.isAdmin(), request.content());
        return ApiResponse.empty();
    }

    @DeleteMapping("/api/comments/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, @AuthenticationPrincipal AuthPrincipal principal) {
        commentService.delete(id, principal.userId(), principal.isAdmin());
        return ApiResponse.empty();
    }
}
