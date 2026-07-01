package com.beenie.backend.domain.post;

import lombok.Builder;
import lombok.Getter;

/** 공개 게시글 목록 조회 조건 (항상 status=PUBLIC, deleted 제외). */
@Getter
@Builder
public class PostListQuery {
    private String categorySlug;
    private String tagName;
    private PostSort sort;
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int size = 10;

    public int offset() {
        return page * size;
    }
}
