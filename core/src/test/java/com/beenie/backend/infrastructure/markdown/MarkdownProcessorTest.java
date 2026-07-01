package com.beenie.backend.infrastructure.markdown;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownProcessorTest {

    private final MarkdownProcessor processor = new MarkdownProcessor();

    @Test
    void convertsMarkdownToHtml() {
        MarkdownResult result = processor.process("# Title\n\nSome **bold** text.");

        assertThat(result.html()).contains("<strong>bold</strong>");
    }

    @Test
    void sanitizesScriptTagsFromRawHtml() {
        MarkdownResult result = processor.process("Hello <script>alert('xss')</script> world");

        assertThat(result.html()).doesNotContain("<script>");
        assertThat(result.html()).doesNotContain("alert(");
    }

    @Test
    void extractsTocFromH2AndH3HeadingsAndInjectsAnchorIds() {
        MarkdownResult result = processor.process("## Introduction\n\ntext\n\n### Details\n\nmore text\n\n## Conclusion");

        assertThat(result.toc()).hasSize(3);
        assertThat(result.toc().get(0).text()).isEqualTo("Introduction");
        assertThat(result.toc().get(0).level()).isEqualTo(2);
        assertThat(result.toc().get(1).text()).isEqualTo("Details");
        assertThat(result.toc().get(1).level()).isEqualTo(3);
        assertThat(result.html()).contains("id=\"" + result.toc().get(0).anchor() + "\"");
    }

    @Test
    void dedupesAnchorsForDuplicateHeadingText() {
        MarkdownResult result = processor.process("## Same\n\ntext\n\n## Same");

        assertThat(result.toc()).hasSize(2);
        assertThat(result.toc().get(0).anchor()).isNotEqualTo(result.toc().get(1).anchor());
    }

    @Test
    void reExtractsTocFromAlreadyRenderedHtml() {
        MarkdownResult result = processor.process("## Heading One");
        var toc = processor.extractToc(result.html());

        assertThat(toc).hasSize(1);
        assertThat(toc.get(0).text()).isEqualTo("Heading One");
    }

    @Test
    void extractsPlainTextSummaryTruncatedToMaxLength() {
        String markdown = "# Title\n\n" + "a".repeat(300);
        String summary = processor.toPlainSummary(markdown, 150);

        assertThat(summary.length()).isLessThanOrEqualTo(150);
    }

    @Test
    void extractsFirstMarkdownImageUrl() {
        String markdown = "intro text\n\n![alt text](https://example.com/image.png)\n\nmore text";
        assertThat(processor.extractFirstImageUrl(markdown)).isEqualTo("https://example.com/image.png");
    }

    @Test
    void returnsNullWhenNoImagePresent() {
        assertThat(processor.extractFirstImageUrl("no images here")).isNull();
    }
}
