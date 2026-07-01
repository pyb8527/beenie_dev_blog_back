package com.beenie.backend.domain.post;

import java.util.List;
import java.util.Optional;

/**
 * 단일 게시글 CRUD Port (JPA 기반 구현). 목록/검색은 {@link PostSearchRepository}(MyBatis) 를 사용한다.
 */
public interface PostRepository {

    Post create(Post post, List<Long> tagIds);

    Post update(Post post, List<Long> tagIds);

    Optional<Post> findById(Long id);

    Optional<Post> findBySlug(String slug);

    boolean existsBySlug(String slug);

    void softDelete(Long id);

    void incrementViewCount(Long id);

    void changeLikeCount(Long id, int delta);

    void changeBookmarkCount(Long id, int delta);

    void reassignCategoryToDefault(Long categoryId);
}
