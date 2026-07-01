package com.beenie.backend.domain.post;

import com.beenie.backend.support.common.response.PageResponse;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 게시글 동적 목록/검색 Port. storage:mybatis 를 통해 구현되며 필터/정렬/페이징/FULLTEXT 검색을 담당한다.
 */
public interface PostSearchRepository {

    PageResponse<PostSummary> findPublicList(PostListQuery query);

    PageResponse<PostSummary> findAdminList(AdminPostListQuery query);

    PageResponse<PostSummary> searchPublic(String keyword, int page, int size);

    Optional<PostNavItem> findPreviousPublic(LocalDateTime createdAt, Long postId);

    Optional<PostNavItem> findNextPublic(LocalDateTime createdAt, Long postId);

    PageResponse<PostSummary> findBookmarkedByUser(Long userId, int page, int size);
}
