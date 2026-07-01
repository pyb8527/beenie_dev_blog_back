package com.beenie.backend.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * User 도메인 모델 (순수 POJO). 영속성 프레임워크에 대한 의존이 없다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String githubId;
    private String username;
    private String avatarUrl;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static User createNew(String githubId, String username, String avatarUrl, UserRole role) {
        return User.builder()
                .githubId(githubId)
                .username(username)
                .avatarUrl(avatarUrl)
                .role(role)
                .build();
    }

    public void updateProfile(String username, String avatarUrl, UserRole role) {
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.role = role;
    }

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }
}
