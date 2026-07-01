package com.beenie.backend.domain.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    /** 기본(미분류) 카테고리 ID. 삭제 불가, 카테고리 삭제 시 게시글이 재배정되는 대상. */
    public static final Long UNCATEGORIZED_ID = 1L;

    private Long id;
    private String name;
    private String slug;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isDefault() {
        return UNCATEGORIZED_ID.equals(id);
    }
}
