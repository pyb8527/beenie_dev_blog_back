package com.beenie.backend.web.search;

import com.beenie.backend.application.search.SearchService;
import com.beenie.backend.domain.post.PostSummary;
import com.beenie.backend.support.common.response.ApiResponse;
import com.beenie.backend.support.common.response.PageResponse;
import com.beenie.backend.web.post.PostSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/api/search")
    public ApiResponse<PageResponse<PostSummaryResponse>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<PostSummary> result = searchService.search(q, page, size);
        PageResponse<PostSummaryResponse> mapped = PageResponse.of(
                result.content().stream().map(PostSummaryResponse::from).toList(),
                result.page(), result.size(), result.totalElements());
        return ApiResponse.success(mapped);
    }
}
