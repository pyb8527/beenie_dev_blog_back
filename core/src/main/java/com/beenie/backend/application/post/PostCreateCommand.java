package com.beenie.backend.application.post;

import java.util.List;

public record PostCreateCommand(
        String title,
        String content,
        Long categoryId,
        List<String> tags,
        String status,
        String thumbnailUrl,
        String slug
) {
}
