package com.beenie.backend.application.feed;

import com.beenie.backend.domain.category.Category;
import com.beenie.backend.domain.category.CategoryRepository;
import com.beenie.backend.domain.post.PostListQuery;
import com.beenie.backend.domain.post.PostSearchRepository;
import com.beenie.backend.domain.post.PostSort;
import com.beenie.backend.domain.post.PostSummary;
import com.beenie.backend.domain.setting.SiteSettingRepository;
import com.beenie.backend.domain.tag.Tag;
import com.beenie.backend.domain.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private static final DateTimeFormatter RFC_822 =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);

    private final PostSearchRepository postSearchRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final SiteSettingRepository siteSettingRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public String generateRss() {
        var setting = siteSettingRepository.get();
        List<PostSummary> posts = postSearchRepository
                .findPublicList(PostListQuery.builder().sort(PostSort.LATEST).page(0).size(20).build())
                .content();

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<rss version=\"2.0\"><channel>\n");
        sb.append("<title>").append(escape(setting.getBlogTitle())).append("</title>\n");
        sb.append("<link>").append(escape(baseUrl)).append("</link>\n");
        sb.append("<description>").append(escape(setting.getBlogDescription())).append("</description>\n");
        for (PostSummary post : posts) {
            String link = baseUrl + "/posts/" + post.slug();
            sb.append("<item>\n");
            sb.append("<title>").append(escape(post.title())).append("</title>\n");
            sb.append("<link>").append(escape(link)).append("</link>\n");
            sb.append("<description>").append(escape(post.summary())).append("</description>\n");
            sb.append("<pubDate>").append(post.createdAt().atZone(java.time.ZoneOffset.UTC).format(RFC_822)).append("</pubDate>\n");
            sb.append("<guid>").append(escape(link)).append("</guid>\n");
            sb.append("</item>\n");
        }
        sb.append("</channel></rss>");
        return sb.toString();
    }

    public String generateSitemap() {
        List<PostSummary> posts = postSearchRepository
                .findPublicList(PostListQuery.builder().sort(PostSort.LATEST).page(0).size(1000).build())
                .content();
        List<Category> categories = categoryRepository.findAll();
        List<Tag> tags = tagRepository.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        appendUrl(sb, baseUrl + "/");
        appendUrl(sb, baseUrl + "/posts");
        for (PostSummary post : posts) {
            appendUrl(sb, baseUrl + "/posts/" + post.slug());
        }
        for (Category category : categories) {
            appendUrl(sb, baseUrl + "/categories/" + category.getSlug());
        }
        for (Tag tag : tags) {
            appendUrl(sb, baseUrl + "/tags/" + tag.getName());
        }
        sb.append("</urlset>");
        return sb.toString();
    }

    private void appendUrl(StringBuilder sb, String loc) {
        sb.append("<url><loc>").append(escape(loc)).append("</loc></url>\n");
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }
}
