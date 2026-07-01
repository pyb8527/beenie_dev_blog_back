package com.beenie.backend.application.auth;

public record AuthResult(Long userId, String role, String accessToken, String refreshToken) {
}
