package com.beenie.backend.web.tag;

import com.beenie.backend.application.tag.TagService;
import com.beenie.backend.support.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ApiResponse<List<TagResponse>> list() {
        return ApiResponse.success(tagService.listAllWithUsageCount().stream().map(TagResponse::from).toList());
    }
}
