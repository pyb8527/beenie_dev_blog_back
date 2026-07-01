package com.beenie.backend.web.activity;

import com.beenie.backend.application.activity.ActivityService;
import com.beenie.backend.domain.post.PostSummary;
import com.beenie.backend.infrastructure.security.AuthPrincipal;
import com.beenie.backend.support.common.response.ApiResponse;
import com.beenie.backend.support.common.response.PageResponse;
import com.beenie.backend.web.post.PostSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping("/api/posts/{postId}/like")
    public ApiResponse<LikeResponse> like(@PathVariable Long postId, @AuthenticationPrincipal AuthPrincipal principal) {
        var result = activityService.toggleLike(principal.userId(), postId);
        return ApiResponse.success(new LikeResponse(result.active(), result.count()));
    }

    @PostMapping("/api/posts/{postId}/bookmark")
    public ApiResponse<BookmarkResponse> bookmark(@PathVariable Long postId, @AuthenticationPrincipal AuthPrincipal principal) {
        var result = activityService.toggleBookmark(principal.userId(), postId);
        return ApiResponse.success(new BookmarkResponse(result.active(), result.count()));
    }

    @GetMapping("/api/me/bookmarks")
    public ApiResponse<PageResponse<PostSummaryResponse>> myBookmarks(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<PostSummary> result = activityService.myBookmarks(principal.userId(), page, size);
        PageResponse<PostSummaryResponse> mapped = PageResponse.of(
                result.content().stream().map(PostSummaryResponse::from).toList(),
                result.page(), result.size(), result.totalElements());
        return ApiResponse.success(mapped);
    }
}
