package com.beenie.backend.infrastructure.persistence;

import com.beenie.backend.domain.post.AdminPostListQuery;
import com.beenie.backend.domain.post.PostListQuery;
import com.beenie.backend.domain.post.PostNavItem;
import com.beenie.backend.domain.post.PostSearchRepository;
import com.beenie.backend.domain.post.PostStatus;
import com.beenie.backend.domain.post.PostSummary;
import com.beenie.backend.storage.mybatis.post.AdminPostListParams;
import com.beenie.backend.storage.mybatis.post.PostListParams;
import com.beenie.backend.storage.mybatis.post.PostNavRow;
import com.beenie.backend.storage.mybatis.post.PostSearchMapper;
import com.beenie.backend.storage.mybatis.post.PostSummaryRow;
import com.beenie.backend.support.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class PostSearchRepositoryAdapter implements PostSearchRepository {

    private final PostSearchMapper postSearchMapper;

    @Override
    public PageResponse<PostSummary> findPublicList(PostListQuery query) {
        PostListParams params = PostListParams.builder()
                .categorySlug(query.getCategorySlug())
                .tagName(query.getTagName())
                .sort(query.getSort() == null ? "LATEST" : query.getSort().name())
                .offset(query.offset())
                .limit(query.getSize())
                .build();
        List<PostSummaryRow> rows = postSearchMapper.findPublicList(params);
        long total = postSearchMapper.countPublicList(params);
        return PageResponse.of(rows.stream().map(PostSearchRepositoryAdapter::toSummary).toList(),
                query.getPage(), query.getSize(), total);
    }

    @Override
    public PageResponse<PostSummary> findAdminList(AdminPostListQuery query) {
        AdminPostListParams params = AdminPostListParams.builder()
                .status(query.getStatus() == null ? null : query.getStatus().name())
                .categoryId(query.getCategoryId())
                .tagName(query.getTagName())
                .dateFrom(query.getDateFrom())
                .dateTo(query.getDateTo())
                .offset(query.offset())
                .limit(query.getSize())
                .build();
        List<PostSummaryRow> rows = postSearchMapper.findAdminList(params);
        long total = postSearchMapper.countAdminList(params);
        return PageResponse.of(rows.stream().map(PostSearchRepositoryAdapter::toSummary).toList(),
                query.getPage(), query.getSize(), total);
    }

    @Override
    public PageResponse<PostSummary> searchPublic(String keyword, int page, int size) {
        int offset = page * size;
        List<PostSummaryRow> rows = postSearchMapper.searchPublic(keyword, offset, size);
        long total = postSearchMapper.countSearchPublic(keyword);
        List<PostSummary> summaries = rows.stream()
                .map(row -> toSummary(row, highlight(row.getSummary(), keyword)))
                .toList();
        return PageResponse.of(summaries, page, size, total);
    }

    @Override
    public Optional<PostNavItem> findPreviousPublic(java.time.LocalDateTime createdAt, Long postId) {
        return Optional.ofNullable(postSearchMapper.findPreviousPublic(createdAt, postId)).map(PostSearchRepositoryAdapter::toNav);
    }

    @Override
    public Optional<PostNavItem> findNextPublic(java.time.LocalDateTime createdAt, Long postId) {
        return Optional.ofNullable(postSearchMapper.findNextPublic(createdAt, postId)).map(PostSearchRepositoryAdapter::toNav);
    }

    @Override
    public PageResponse<PostSummary> findBookmarkedByUser(Long userId, int page, int size) {
        int offset = page * size;
        List<PostSummaryRow> rows = postSearchMapper.findBookmarkedByUser(userId, offset, size);
        long total = postSearchMapper.countBookmarkedByUser(userId);
        return PageResponse.of(rows.stream().map(PostSearchRepositoryAdapter::toSummary).toList(), page, size, total);
    }

    private static PostSummary toSummary(PostSummaryRow row) {
        return toSummary(row, null);
    }

    private static PostSummary toSummary(PostSummaryRow row, String highlight) {
        List<String> tags = row.getTagsCsv() == null || row.getTagsCsv().isBlank()
                ? List.of()
                : Arrays.asList(row.getTagsCsv().split(","));
        return new PostSummary(
                row.getId(), row.getTitle(), row.getSlug(), row.getSummary(), row.getThumbnailUrl(),
                PostStatus.valueOf(row.getStatus()), row.getViewCount(), row.getLikeCount(), row.getBookmarkCount(),
                row.getCategoryId(), row.getCategoryName(), row.getCategorySlug(), tags, highlight,
                row.getCreatedAt(), row.getUpdatedAt());
    }

    private static PostNavItem toNav(PostNavRow row) {
        return new PostNavItem(row.getId(), row.getTitle(), row.getSlug());
    }

    private static String highlight(String text, String keyword) {
        if (text == null || keyword == null || keyword.isBlank()) {
            return text;
        }
        Pattern pattern = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);
        return pattern.matcher(text).replaceAll(matchResult -> "<mark>" + matchResult.group() + "</mark>");
    }
}
