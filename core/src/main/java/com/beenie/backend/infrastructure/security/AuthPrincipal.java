package com.beenie.backend.infrastructure.security;

/**
 * JWT 인증 후 SecurityContext 에 저장되는 인증 주체.
 */
public record AuthPrincipal(Long userId, String role) {

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
