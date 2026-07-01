package com.beenie.backend.domain.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    private Long id;
    private String title;
    private String slug;
    private String content;
    private String htmlContent;
    private String summary;
    private String thumbnailUrl;
    private PostStatus status;
    private long viewCount;
    private long likeCount;
    private long bookmarkCount;
    private Long categoryId;
    private Long authorId;
    private List<String> tags;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean isPublic() {
        return status == PostStatus.PUBLIC;
    }
}
