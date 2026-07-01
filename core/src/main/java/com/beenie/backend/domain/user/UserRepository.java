package com.beenie.backend.domain.user;

import java.util.Optional;

/**
 * User 영속성 Port. storage:jpa 기반 Adapter가 core/infrastructure/persistence 에서 구현한다.
 */
public interface UserRepository {

    Optional<User> findById(Long id);

    Optional<User> findByGithubId(String githubId);

    User save(User user);
}
