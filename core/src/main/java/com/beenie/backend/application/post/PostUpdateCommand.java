package com.beenie.backend.application.post;

import java.util.List;

public record PostUpdateCommand(
        String title,
        String content,
        Long categoryId,
        List<String> tags,
        String status,
        String thumbnailUrl,
        boolean changeSlug
) {
}
