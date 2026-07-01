package com.beenie.backend.application.post;

import com.beenie.backend.domain.category.Category;
import com.beenie.backend.domain.post.Post;
import com.beenie.backend.domain.post.PostNavItem;
import com.beenie.backend.infrastructure.markdown.TocItem;

import java.util.List;

public record PostDetailResult(
        Post post,
        Category category,
        List<TocItem> toc,
        PostNavItem previous,
        PostNavItem next,
        boolean liked,
        boolean bookmarked
) {
}
