package com.beenie.backend.infrastructure.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown -> HTML 변환(Flexmark) + XSS sanitize(OWASP allowlist) + TOC(H2/H3) 추출.
 */
@Component
public class MarkdownProcessor {

    private static final Pattern HEADING_PATTERN = Pattern.compile("<(h2|h3)>(.*?)</\\1>", Pattern.DOTALL);
    private static final Pattern HEADING_WITH_ID_PATTERN =
            Pattern.compile("<(h2|h3)\\s+id=\"([^\"]+)\">(.*?)</\\1>", Pattern.DOTALL);
    private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final Pattern MD_IMAGE_PATTERN = Pattern.compile("!\\[[^]]*]\\(([^)\\s]+)");
    private static final Pattern NON_WORD_PATTERN = Pattern.compile("[^\\w가-힣\\s-]");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("[\\s]+");

    private static final PolicyFactory POLICY = Sanitizers.FORMATTING
            .and(Sanitizers.BLOCKS)
            .and(Sanitizers.LINKS)
            .and(Sanitizers.IMAGES)
            .and(Sanitizers.TABLES)
            .and(Sanitizers.STYLES);

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public MarkdownResult process(String markdown) {
        Node document = parser.parse(markdown == null ? "" : markdown);
        String rawHtml = renderer.render(document);
        String sanitized = POLICY.sanitize(rawHtml);
        return injectAnchors(sanitized);
    }

    public String toPlainSummary(String markdownOrHtml, int maxLength) {
        if (markdownOrHtml == null) {
            return "";
        }
        String plain = TAG_PATTERN.matcher(markdownOrHtml).replaceAll(" ");
        plain = plain.replaceAll("[#>*`_\\-\\[\\]()]", " ");
        plain = WHITESPACE_PATTERN.matcher(plain).replaceAll(" ").trim();
        return plain.length() > maxLength ? plain.substring(0, maxLength) : plain;
    }

    /** 이미 id 가 부여된(저장된) HTML 에서 TOC 목록만 다시 추출한다 (읽기 시점 재사용). */
    public List<TocItem> extractToc(String html) {
        if (html == null) {
            return List.of();
        }
        List<TocItem> toc = new ArrayList<>();
        Matcher matcher = HEADING_WITH_ID_PATTERN.matcher(html);
        while (matcher.find()) {
            String tag = matcher.group(1);
            String anchor = matcher.group(2);
            String text = TAG_PATTERN.matcher(matcher.group(3)).replaceAll("").trim();
            toc.add(new TocItem(text, anchor, "h2".equals(tag) ? 2 : 3));
        }
        return toc;
    }

    public String extractFirstImageUrl(String markdown) {
        if (markdown == null) {
            return null;
        }
        Matcher matcher = MD_IMAGE_PATTERN.matcher(markdown);
        if (matcher.find()) {
            return matcher.group(1);
        }
        Matcher htmlImg = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']").matcher(markdown);
        return htmlImg.find() ? htmlImg.group(1) : null;
    }

    private MarkdownResult injectAnchors(String sanitizedHtml) {
        List<TocItem> toc = new ArrayList<>();
        Set<String> usedAnchors = new HashSet<>();
        Matcher matcher = HEADING_PATTERN.matcher(sanitizedHtml);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String tag = matcher.group(1);
            String innerHtml = matcher.group(2);
            String text = TAG_PATTERN.matcher(innerHtml).replaceAll("").trim();
            String anchor = uniqueSlug(slugify(text), usedAnchors);
            int level = "h2".equals(tag) ? 2 : 3;
            toc.add(new TocItem(text, anchor, level));
            matcher.appendReplacement(result,
                    Matcher.quoteReplacement("<" + tag + " id=\"" + anchor + "\">" + innerHtml + "</" + tag + ">"));
        }
        matcher.appendTail(result);
        return new MarkdownResult(result.toString(), toc);
    }

    private String slugify(String text) {
        String noSpecial = NON_WORD_PATTERN.matcher(text.toLowerCase(Locale.ROOT)).replaceAll("");
        String slug = WHITESPACE_PATTERN.matcher(noSpecial.trim()).replaceAll("-");
        return slug.isBlank() ? "section" : slug;
    }

    private String uniqueSlug(String base, Set<String> used) {
        String candidate = base;
        int suffix = 1;
        while (used.contains(candidate)) {
            candidate = base + "-" + suffix++;
        }
        used.add(candidate);
        return candidate;
    }
}
