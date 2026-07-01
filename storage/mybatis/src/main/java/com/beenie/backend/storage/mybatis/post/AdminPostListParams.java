package com.beenie.backend.storage.mybatis.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPostListParams {
    private String status;
    private Long categoryId;
    private String tagName;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private int offset;
    private int limit;
}
