package com.beenie.backend.storage.mybatis.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostNavRow {
    private Long id;
    private String title;
    private String slug;
}
