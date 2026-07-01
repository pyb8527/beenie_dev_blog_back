package com.beenie.backend.web.category;

import com.beenie.backend.application.category.CategoryService;
import com.beenie.backend.domain.category.Category;
import com.beenie.backend.support.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ApiResponse<CategoryDetailResponse> create(@Valid @RequestBody CategoryRequest request) {
        Category category = categoryService.create(request.name());
        return ApiResponse.success(CategoryDetailResponse.from(category));
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryDetailResponse> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        Category category = categoryService.update(id, request.name());
        return ApiResponse.success(CategoryDetailResponse.from(category));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.empty();
    }
}
