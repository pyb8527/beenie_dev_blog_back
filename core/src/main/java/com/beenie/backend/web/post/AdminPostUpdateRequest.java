package com.beenie.backend.web.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AdminPostUpdateRequest(
        @NotBlank(message = "제목은 필수입니다.")
        @Size(min = 1, max = 200, message = "제목은 1~200자여야 합니다.")
        String title,

        @NotBlank(message = "본문은 필수입니다.")
        @Size(min = 1, max = 50000, message = "본문은 1~50000자여야 합니다.")
        String content,

        Long categoryId,

        List<String> tags,

        @NotNull(message = "상태는 필수입니다.")
        String status,

        String thumbnailUrl,

        boolean changeSlug
) {
}
