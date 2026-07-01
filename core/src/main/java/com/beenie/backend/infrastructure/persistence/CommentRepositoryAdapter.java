package com.beenie.backend.infrastructure.persistence;

import com.beenie.backend.domain.comment.Comment;
import com.beenie.backend.domain.comment.CommentRepository;
import com.beenie.backend.storage.jpa.entity.CommentJpaEntity;
import com.beenie.backend.storage.jpa.entity.UserJpaEntity;
import com.beenie.backend.storage.jpa.repository.CommentJpaRepository;
import com.beenie.backend.storage.jpa.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentRepositoryAdapter implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public Comment save(Comment comment) {
        CommentJpaEntity entity = CommentJpaEntity.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .authorId(comment.getAuthorId())
                .parentId(comment.getParentId())
                .content(comment.getContent())
                .deleted(comment.isDeleted())
                .deletedAt(comment.getDeletedAt())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
        CommentJpaEntity saved = commentJpaRepository.save(entity);
        return toDomain(saved, authorMap(List.of(saved)));
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentJpaRepository.findById(id).map(e -> toDomain(e, authorMap(List.of(e))));
    }

    @Override
    public List<Comment> findAllByPostId(Long postId) {
        List<CommentJpaEntity> entities = commentJpaRepository.findAllByPostIdOrderByCreatedAtAsc(postId);
        Map<Long, UserJpaEntity> authors = authorMap(entities);
        return entities.stream().map(e -> toDomain(e, authors)).toList();
    }

    @Override
    public void softDelete(Long id) {
        commentJpaRepository.softDelete(id, LocalDateTime.now());
    }

    @Override
    public void hardDelete(Long id) {
        commentJpaRepository.deleteById(id);
    }

    @Override
    public long countByParentId(Long parentId) {
        return commentJpaRepository.countByParentId(parentId);
    }

    @Override
    public void softDeleteAllByPostId(Long postId) {
        commentJpaRepository.softDeleteAllByPostId(postId, LocalDateTime.now());
    }

    @Override
    public long countAll() {
        return commentJpaRepository.count();
    }

    @Override
    public List<Comment> findAllForAdmin(Long postId, int offset, int limit) {
        int page = limit == 0 ? 0 : offset / limit;
        List<CommentJpaEntity> entities = commentJpaRepository.findAllForAdmin(postId, PageRequest.of(page, limit));
        Map<Long, UserJpaEntity> authors = authorMap(entities);
        return entities.stream().map(e -> toDomain(e, authors)).toList();
    }

    @Override
    public long countForAdmin(Long postId) {
        return commentJpaRepository.countForAdmin(postId);
    }

    private Map<Long, UserJpaEntity> authorMap(List<CommentJpaEntity> entities) {
        List<Long> ids = entities.stream().map(CommentJpaEntity::getAuthorId).distinct().toList();
        if (ids.isEmpty()) {
            return Map.of();
        }
        return userJpaRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(UserJpaEntity::getId, u -> u));
    }

    private Comment toDomain(CommentJpaEntity entity, Map<Long, UserJpaEntity> authors) {
        UserJpaEntity author = authors.get(entity.getAuthorId());
        return Comment.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .authorId(entity.getAuthorId())
                .authorName(author != null ? author.getUsername() : "알 수 없음")
                .authorAvatarUrl(author != null ? author.getAvatarUrl() : null)
                .parentId(entity.getParentId())
                .content(entity.getContent())
                .deleted(entity.isDeleted())
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
