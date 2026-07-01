package com.beenie.backend.storage.mybatis.post;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostSearchMapper {

    List<PostSummaryRow> findPublicList(@Param("p") PostListParams params);

    long countPublicList(@Param("p") PostListParams params);

    List<PostSummaryRow> findAdminList(@Param("p") AdminPostListParams params);

    long countAdminList(@Param("p") AdminPostListParams params);

    List<PostSummaryRow> searchPublic(@Param("keyword") String keyword,
                                       @Param("offset") int offset,
                                       @Param("limit") int limit);

    long countSearchPublic(@Param("keyword") String keyword);

    PostNavRow findPreviousPublic(@Param("createdAt") LocalDateTime createdAt, @Param("postId") Long postId);

    PostNavRow findNextPublic(@Param("createdAt") LocalDateTime createdAt, @Param("postId") Long postId);

    List<PostSummaryRow> findBookmarkedByUser(@Param("userId") Long userId,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);

    long countBookmarkedByUser(@Param("userId") Long userId);
}
