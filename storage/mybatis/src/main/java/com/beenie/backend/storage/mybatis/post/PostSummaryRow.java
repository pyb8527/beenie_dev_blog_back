package com.beenie.backend.storage.mybatis.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryRow {
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String thumbnailUrl;
    private String status;
    private long viewCount;
    private long likeCount;
    private long bookmarkCount;
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private String tagsCsv;
    private String highlight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
