package com.beenie.backend.web.post;

import com.beenie.backend.application.post.PostCreateCommand;
import com.beenie.backend.application.post.PostService;
import com.beenie.backend.application.post.PostUpdateCommand;
import com.beenie.backend.domain.post.AdminPostListQuery;
import com.beenie.backend.domain.post.Post;
import com.beenie.backend.domain.post.PostStatus;
import com.beenie.backend.domain.post.PostSummary;
import com.beenie.backend.infrastructure.security.AuthPrincipal;
import com.beenie.backend.support.common.response.ApiResponse;
import com.beenie.backend.support.common.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
public class AdminPostController {

    private final PostService postService;

    @GetMapping
    public ApiResponse<PageResponse<PostSummaryResponse>> list(
            @RequestParam(required = false) PostStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        AdminPostListQuery query = AdminPostListQuery.builder()
                .status(status)
                .categoryId(categoryId)
                .tagName(tag)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .page(page)
                .size(size)
                .build();

        PageResponse<PostSummary> result = postService.listAdmin(query);
        PageResponse<PostSummaryResponse> mapped = PageResponse.of(
                result.content().stream().map(PostSummaryResponse::from).toList(),
                result.page(), result.size(), result.totalElements());
        return ApiResponse.success(mapped);
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminPostResponse> getById(@PathVariable Long id) {
        Post post = postService.getByIdForAdmin(id);
        return ApiResponse.success(AdminPostResponse.from(post));
    }

    @PostMapping
    public ApiResponse<AdminPostResponse> create(@Valid @RequestBody AdminPostCreateRequest request,
                                                  @AuthenticationPrincipal AuthPrincipal principal) {
        PostCreateCommand command = new PostCreateCommand(request.title(), request.content(), request.categoryId(),
                request.tags(), request.status(), request.thumbnailUrl(), request.slug());
        Post created = postService.create(command, principal.userId());
        return ApiResponse.success(AdminPostResponse.from(created));
    }

    @PutMapping("/{id}")
    public ApiResponse<AdminPostResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody AdminPostUpdateRequest request) {
        PostUpdateCommand command = new PostUpdateCommand(request.title(), request.content(), request.categoryId(),
                request.tags(), request.status(), request.thumbnailUrl(), request.changeSlug());
        Post updated = postService.update(id, command);
        return ApiResponse.success(AdminPostResponse.from(updated));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        postService.updateStatus(id, status);
        return ApiResponse.empty();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        postService.delete(id);
        return ApiResponse.empty();
    }
}
