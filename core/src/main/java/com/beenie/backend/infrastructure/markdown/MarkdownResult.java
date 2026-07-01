package com.beenie.backend.infrastructure.markdown;

import java.util.List;

public record MarkdownResult(String html, List<TocItem> toc) {
}
