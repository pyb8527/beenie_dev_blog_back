package com.beenie.backend.web.auth;

import com.beenie.backend.domain.user.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String avatarUrl,
        String role,
        LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getAvatarUrl(),
                user.getRole().name(), user.getCreatedAt());
    }
}
