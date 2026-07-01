package com.beenie.backend.web.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "카테고리 이름은 필수입니다.")
        @Size(min = 1, max = 100, message = "카테고리 이름은 1~100자여야 합니다.")
        String name
) {
}
