package com.beenie.backend.web.post;

import com.beenie.backend.application.post.PostDetailResult;
import com.beenie.backend.application.post.PostService;
import com.beenie.backend.domain.post.PostListQuery;
import com.beenie.backend.domain.post.PostSort;
import com.beenie.backend.domain.post.PostSummary;
import com.beenie.backend.infrastructure.security.AuthPrincipal;
import com.beenie.backend.support.common.response.ApiResponse;
import com.beenie.backend.support.common.response.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ApiResponse<PageResponse<PostSummaryResponse>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PostListQuery query = PostListQuery.builder()
                .categorySlug(category)
                .tagName(tag)
                .sort(PostSort.from(sort))
                .page(page)
                .size(size)
                .build();

        PageResponse<PostSummary> result = postService.listPublic(query);
        return ApiResponse.success(mapPage(result));
    }

    @GetMapping("/{slug}")
    public ApiResponse<PostDetailResponse> detail(@PathVariable String slug,
                                                    @AuthenticationPrincipal AuthPrincipal principal,
                                                    HttpServletRequest request) {
        boolean isAdmin = principal != null && principal.isAdmin();
        Long requesterId = principal != null ? principal.userId() : null;
        PostDetailResult result = postService.getDetail(slug, isAdmin, requesterId, resolveIp(request));
        return ApiResponse.success(PostDetailResponse.from(result));
    }

    private PageResponse<PostSummaryResponse> mapPage(PageResponse<PostSummary> page) {
        return PageResponse.of(page.content().stream().map(PostSummaryResponse::from).toList(),
                page.page(), page.size(), page.totalElements());
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
