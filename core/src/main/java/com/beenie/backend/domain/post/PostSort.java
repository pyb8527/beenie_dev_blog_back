package com.beenie.backend.domain.post;

public enum PostSort {
    LATEST,
    POPULAR,
    LIKED;

    public static PostSort from(String raw) {
        if (raw == null || raw.isBlank()) {
            return LATEST;
        }
        return switch (raw.toLowerCase()) {
            case "popular" -> POPULAR;
            case "liked" -> LIKED;
            default -> LATEST;
        };
    }
}
