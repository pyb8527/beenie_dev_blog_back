package com.beenie.backend.web.tag;

import com.beenie.backend.domain.tag.TagCount;

public record TagResponse(Long id, String name, long useCount) {
    public static TagResponse from(TagCount count) {
        return new TagResponse(count.id(), count.name(), count.useCount());
    }
}
