package com.beenie.backend.infrastructure.persistence;

import com.beenie.backend.domain.post.Post;
import com.beenie.backend.domain.post.PostRepository;
import com.beenie.backend.domain.post.PostStatus;
import com.beenie.backend.storage.jpa.entity.PostJpaEntity;
import com.beenie.backend.storage.jpa.entity.PostTagJpaEntity;
import com.beenie.backend.storage.jpa.repository.PostJpaRepository;
import com.beenie.backend.storage.jpa.repository.PostTagJpaRepository;
import com.beenie.backend.storage.jpa.repository.TagJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostRepositoryAdapter implements PostRepository {

    private final PostJpaRepository postJpaRepository;
    private final PostTagJpaRepository postTagJpaRepository;
    private final TagJpaRepository tagJpaRepository;

    @Override
    public Post create(Post post, List<Long> tagIds) {
        PostJpaEntity entity = toEntity(post);
        PostJpaEntity saved = postJpaRepository.save(entity);
        replaceTags(saved.getId(), tagIds);
        return findById(saved.getId()).orElseThrow();
    }

    @Override
    public Post update(Post post, List<Long> tagIds) {
        PostJpaEntity entity = toEntity(post);
        PostJpaEntity saved = postJpaRepository.save(entity);
        replaceTags(saved.getId(), tagIds);
        return findById(saved.getId()).orElseThrow();
    }

    private void replaceTags(Long postId, List<Long> tagIds) {
        postTagJpaRepository.deleteAllByPostId(postId);
        if (tagIds != null && !tagIds.isEmpty()) {
            List<PostTagJpaEntity> rows = tagIds.stream().distinct()
                    .map(tagId -> new PostTagJpaEntity(postId, tagId))
                    .toList();
            postTagJpaRepository.saveAll(rows);
        }
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Post> findBySlug(String slug) {
        return postJpaRepository.findBySlug(slug).map(this::toDomain);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return postJpaRepository.existsBySlug(slug);
    }

    @Override
    public void softDelete(Long id) {
        postJpaRepository.softDelete(id, LocalDateTime.now());
    }

    @Override
    public void incrementViewCount(Long id) {
        postJpaRepository.incrementViewCount(id);
    }

    @Override
    public void changeLikeCount(Long id, int delta) {
        postJpaRepository.changeLikeCount(id, delta);
    }

    @Override
    public void changeBookmarkCount(Long id, int delta) {
        postJpaRepository.changeBookmarkCount(id, delta);
    }

    @Override
    public void reassignCategoryToDefault(Long categoryId) {
        postJpaRepository.reassignCategoryToDefault(categoryId);
    }

    private Post toDomain(PostJpaEntity entity) {
        List<String> tags = tagJpaRepository.findNamesByPostId(entity.getId());
        return Post.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .content(entity.getContent())
                .htmlContent(entity.getHtmlContent())
                .summary(entity.getSummary())
                .thumbnailUrl(entity.getThumbnailUrl())
                .status(PostStatus.valueOf(entity.getStatus()))
                .viewCount(entity.getViewCount())
                .likeCount(entity.getLikeCount())
                .bookmarkCount(entity.getBookmarkCount())
                .categoryId(entity.getCategoryId())
                .authorId(entity.getAuthorId())
                .tags(tags)
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private PostJpaEntity toEntity(Post post) {
        return PostJpaEntity.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .content(post.getContent())
                .htmlContent(post.getHtmlContent())
                .summary(post.getSummary())
                .thumbnailUrl(post.getThumbnailUrl())
                .status(post.getStatus().name())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .bookmarkCount(post.getBookmarkCount())
                .categoryId(post.getCategoryId())
                .authorId(post.getAuthorId())
                .deletedAt(post.getDeletedAt())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
