package com.beenie.backend.application.post;

import com.beenie.backend.domain.category.Category;
import com.beenie.backend.domain.category.CategoryRepository;
import com.beenie.backend.domain.comment.CommentRepository;
import com.beenie.backend.domain.activity.PostBookmarkRepository;
import com.beenie.backend.domain.activity.PostLikeRepository;
import com.beenie.backend.domain.event.PostEventPublisher;
import com.beenie.backend.domain.post.AdminPostListQuery;
import com.beenie.backend.domain.post.Post;
import com.beenie.backend.domain.post.PostListQuery;
import com.beenie.backend.domain.post.PostNavItem;
import com.beenie.backend.domain.post.PostRepository;
import com.beenie.backend.domain.post.PostSearchRepository;
import com.beenie.backend.domain.post.PostSort;
import com.beenie.backend.domain.post.PostStatus;
import com.beenie.backend.domain.post.PostSummary;
import com.beenie.backend.domain.post.ViewCountDedupRepository;
import com.beenie.backend.domain.tag.Tag;
import com.beenie.backend.domain.tag.TagRepository;
import com.beenie.backend.infrastructure.markdown.MarkdownProcessor;
import com.beenie.backend.infrastructure.markdown.MarkdownResult;
import com.beenie.backend.storage.redis.PostListCacheStore;
import com.beenie.backend.support.common.response.PageResponse;
import com.beenie.backend.support.exception.BusinessException;
import com.beenie.backend.support.exception.ErrorCode;
import com.beenie.backend.support.util.SlugGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostSearchRepository postSearchRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final MarkdownProcessor markdownProcessor;
    private final PostEventPublisher postEventPublisher;
    private final ViewCountDedupRepository viewCountDedupRepository;
    private final PostListCacheStore postListCacheStore;
    private final ObjectMapper objectMapper;
    private final PostLikeRepository postLikeRepository;
    private final PostBookmarkRepository postBookmarkRepository;

    public PageResponse<PostSummary> listPublic(PostListQuery query) {
        boolean cacheable = query.getPage() == 0 && query.getCategorySlug() == null && query.getTagName() == null;
        String cacheKey = "sort:" + (query.getSort() == null ? PostSort.LATEST : query.getSort());

        if (cacheable) {
            Optional<PageResponse<PostSummary>> cached = readCache(cacheKey);
            if (cached.isPresent()) {
                return cached.get();
            }
        }

        PageResponse<PostSummary> result = postSearchRepository.findPublicList(query);

        if (cacheable) {
            writeCache(cacheKey, result);
        }
        return result;
    }

    public PageResponse<PostSummary> listAdmin(AdminPostListQuery query) {
        return postSearchRepository.findAdminList(query);
    }

    public PostDetailResult getDetail(String slug, boolean requesterIsAdmin, Long requesterId, String clientIp) {
        Post post = postRepository.findBySlug(slug)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.isPublic() && !requesterIsAdmin) {
            throw new BusinessException(ErrorCode.POST_FORBIDDEN);
        }

        if (post.isPublic() && clientIp != null) {
            recordView(post.getId(), clientIp);
        }

        Category category = post.getCategoryId() == null
                ? null
                : categoryRepository.findById(post.getCategoryId()).orElse(null);

        List<com.beenie.backend.infrastructure.markdown.TocItem> toc = markdownProcessor.extractToc(post.getHtmlContent());

        PostNavItem previous = post.isPublic()
                ? postSearchRepository.findPreviousPublic(post.getCreatedAt(), post.getId()).orElse(null)
                : null;
        PostNavItem next = post.isPublic()
                ? postSearchRepository.findNextPublic(post.getCreatedAt(), post.getId()).orElse(null)
                : null;

        boolean liked = requesterId != null && postLikeRepository.exists(requesterId, post.getId());
        boolean bookmarked = requesterId != null && postBookmarkRepository.exists(requesterId, post.getId());

        return new PostDetailResult(post, category, toc, previous, next, liked, bookmarked);
    }

    public Post getByIdForAdmin(Long id) {
        return postRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

    private void recordView(Long postId, String clientIp) {
        try {
            if (viewCountDedupRepository.markViewedIfAbsent(postId, clientIp)) {
                postEventPublisher.publishViewIncrement(postId);
            }
        } catch (Exception e) {
            log.warn("조회수 처리 실패 postId={}", postId, e);
        }
    }

    @Transactional
    public Post create(PostCreateCommand command, Long authorId) {
        PostStatus status = parseStatus(command.status());
        MarkdownResult rendered = markdownProcessor.process(command.content());
        Long categoryId = resolveCategoryId(command.categoryId());
        List<Long> tagIds = resolveTagIds(command.tags());
        String slug = resolveSlugForCreate(command.slug(), command.title());
        String thumbnail = resolveThumbnail(command.thumbnailUrl(), command.content());

        Post post = Post.builder()
                .title(command.title())
                .slug(slug)
                .content(command.content())
                .htmlContent(rendered.html())
                .summary(markdownProcessor.toPlainSummary(command.content(), 150))
                .thumbnailUrl(thumbnail)
                .status(status)
                .categoryId(categoryId)
                .authorId(authorId)
                .build();

        Post created = postRepository.create(post, tagIds);
        afterChange();
        return created;
    }

    @Transactional
    public Post update(Long id, PostUpdateCommand command) {
        Post existing = postRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        PostStatus status = parseStatus(command.status());
        MarkdownResult rendered = markdownProcessor.process(command.content());
        Long categoryId = resolveCategoryId(command.categoryId());
        List<Long> tagIds = resolveTagIds(command.tags());
        String thumbnail = resolveThumbnail(command.thumbnailUrl(), command.content());

        String slug = existing.getSlug();
        if (command.changeSlug()) {
            slug = resolveSlugForUpdate(existing.getSlug(), command.title());
        }

        existing.setTitle(command.title());
        existing.setSlug(slug);
        existing.setContent(command.content());
        existing.setHtmlContent(rendered.html());
        existing.setSummary(markdownProcessor.toPlainSummary(command.content(), 150));
        existing.setThumbnailUrl(thumbnail);
        existing.setStatus(status);
        existing.setCategoryId(categoryId);

        Post updated = postRepository.update(existing, tagIds);
        afterChange();
        return updated;
    }

    @Transactional
    public void updateStatus(Long id, String statusRaw) {
        Post existing = postRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        existing.setStatus(parseStatus(statusRaw));
        postRepository.update(existing, existing.getTags() == null ? List.of() : resolveTagIds(existing.getTags()));
        afterChange();
    }

    @Transactional
    public void delete(Long id) {
        postRepository.findById(id).filter(p -> !p.isDeleted())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        postRepository.softDelete(id);
        commentRepository.softDeleteAllByPostId(id);
        afterChange();
    }

    private void afterChange() {
        postListCacheStore.evictAll();
        try {
            postEventPublisher.publishPostChanged();
        } catch (Exception e) {
            log.warn("게시글 변경 이벤트 발행 실패", e);
        }
    }

    private PostStatus parseStatus(String raw) {
        try {
            return PostStatus.valueOf(raw);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private Long resolveCategoryId(Long categoryId) {
        if (categoryId == null) {
            return Category.UNCATEGORIZED_ID;
        }
        categoryRepository.findById(categoryId).orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        return categoryId;
    }

    private List<Long> resolveTagIds(List<String> tagNames) {
        List<Tag> tags = tagRepository.resolveOrCreate(tagNames == null ? List.of() : tagNames);
        return tags.stream().map(Tag::getId).toList();
    }

    private String resolveSlugForCreate(String requestedSlug, String title) {
        String base = (requestedSlug != null && !requestedSlug.isBlank()) ? SlugGenerator.generate(requestedSlug)
                : SlugGenerator.generate(title);
        return ensureUniqueSlug(base);
    }

    private String resolveSlugForUpdate(String currentSlug, String title) {
        String base = SlugGenerator.generate(title);
        if (base.equals(currentSlug)) {
            return currentSlug;
        }
        return ensureUniqueSlug(base);
    }

    private String ensureUniqueSlug(String base) {
        String candidate = base;
        int suffix = 1;
        while (postRepository.existsBySlug(candidate)) {
            candidate = SlugGenerator.withSuffix(base, suffix++);
        }
        return candidate;
    }

    private String resolveThumbnail(String requested, String markdownContent) {
        if (requested != null && !requested.isBlank()) {
            return requested;
        }
        return markdownProcessor.extractFirstImageUrl(markdownContent);
    }

    private void writeCache(String key, PageResponse<PostSummary> value) {
        try {
            postListCacheStore.put(key, objectMapper.writeValueAsString(value));
        } catch (Exception e) {
            log.warn("게시글 목록 캐시 저장 실패 key={}", key, e);
        }
    }

    private static final com.fasterxml.jackson.core.type.TypeReference<PageResponse<PostSummary>> PAGE_TYPE =
            new com.fasterxml.jackson.core.type.TypeReference<>() {
            };

    private Optional<PageResponse<PostSummary>> readCache(String key) {
        try {
            return postListCacheStore.get(key)
                    .map(json -> {
                        try {
                            return objectMapper.readValue(json, PAGE_TYPE);
                        } catch (Exception e) {
                            return null;
                        }
                    });
        } catch (Exception e) {
            log.warn("게시글 목록 캐시 조회 실패 key={}", key, e);
            return Optional.empty();
        }
    }
}
