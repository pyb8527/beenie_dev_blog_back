package com.beenie.backend.infrastructure.persistence;

import com.beenie.backend.domain.user.User;
import com.beenie.backend.domain.user.UserRepository;
import com.beenie.backend.domain.user.UserRole;
import com.beenie.backend.storage.jpa.entity.UserJpaEntity;
import com.beenie.backend.storage.jpa.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(UserRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<User> findByGithubId(String githubId) {
        return userJpaRepository.findByGithubId(githubId).map(UserRepositoryAdapter::toDomain);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = toEntity(user);
        UserJpaEntity saved = userJpaRepository.save(entity);
        return toDomain(saved);
    }

    private static User toDomain(UserJpaEntity entity) {
        return User.builder()
                .id(entity.getId())
                .githubId(entity.getGithubId())
                .username(entity.getUsername())
                .avatarUrl(entity.getAvatarUrl())
                .role(UserRole.valueOf(entity.getRole()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private static UserJpaEntity toEntity(User user) {
        return UserJpaEntity.builder()
                .id(user.getId())
                .githubId(user.getGithubId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
