package com.beenie.backend.storage.jpa.repository;

import com.beenie.backend.storage.jpa.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByGithubId(String githubId);
}
