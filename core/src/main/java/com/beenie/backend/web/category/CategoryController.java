package com.beenie.backend.web.category;

import com.beenie.backend.application.category.CategoryService;
import com.beenie.backend.support.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryResponse>> list() {
        List<CategoryResponse> response = categoryService.listAllWithPostCount().stream()
                .map(CategoryResponse::from)
                .toList();
        return ApiResponse.success(response);
    }
}
