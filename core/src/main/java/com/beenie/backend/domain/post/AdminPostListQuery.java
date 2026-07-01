package com.beenie.backend.domain.post;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/** 관리자 게시글 목록 조회 조건 (비공개/임시저장 포함, 상태·카테고리·태그·기간 필터). */
@Getter
@Builder
public class AdminPostListQuery {
    private PostStatus status;
    private Long categoryId;
    private String tagName;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int size = 20;

    public int offset() {
        return page * size;
    }
}
