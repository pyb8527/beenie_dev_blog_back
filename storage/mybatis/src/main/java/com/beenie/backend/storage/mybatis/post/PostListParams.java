package com.beenie.backend.storage.mybatis.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostListParams {
    private String categorySlug;
    private String tagName;
    private String sort; // LATEST | POPULAR | LIKED
    private int offset;
    private int limit;
}
