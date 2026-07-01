package com.beenie.backend.web.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(
        @NotBlank(message = "댓글 내용은 필수입니다.")
        @Size(min = 1, max = 500, message = "댓글은 1~500자여야 합니다.")
        String content
) {
}
