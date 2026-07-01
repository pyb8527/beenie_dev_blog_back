package com.beenie.backend.web.tag;

import com.beenie.backend.application.tag.TagService;
import com.beenie.backend.domain.tag.Tag;
import com.beenie.backend.support.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {

    private final TagService tagService;

    @PutMapping("/{id}")
    public ApiResponse<TagDetailResponse> update(@PathVariable Long id, @Valid @RequestBody TagRequest request) {
        Tag tag = tagService.update(id, request.name());
        return ApiResponse.success(new TagDetailResponse(tag.getId(), tag.getName()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ApiResponse.empty();
    }

    public record TagDetailResponse(Long id, String name) {
    }
}
