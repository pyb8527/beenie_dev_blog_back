package com.beenie.backend.application.comment;

import com.beenie.backend.domain.comment.Comment;
import com.beenie.backend.domain.comment.CommentRepository;
import com.beenie.backend.domain.post.PostRepository;
import com.beenie.backend.support.exception.BusinessException;
import com.beenie.backend.support.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public List<Comment> listByPost(Long postId) {
        return commentRepository.findAllByPostId(postId);
    }

    @Transactional
    public Comment create(Long postId, Long authorId, String content, Long parentId) {
        if (parentId != null) {
            Comment parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
            if (parent.isDeleted()) {
                throw new BusinessException(ErrorCode.COMMENT_PARENT_DELETED);
            }
            if (!parent.isTopLevel()) {
                throw new BusinessException(ErrorCode.COMMENT_PARENT_NOT_TOP_LEVEL);
            }
        }

        Comment comment = Comment.builder()
                .postId(postId)
                .authorId(authorId)
                .parentId(parentId)
                .content(content)
                .deleted(false)
                .build();
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment update(Long id, Long requesterId, boolean isAdmin, String content) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        if (comment.isDeleted()) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }
        ensureOwnerOrAdmin(comment, requesterId, isAdmin);
        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Transactional
    public void delete(Long id, Long requesterId, boolean isAdmin) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        ensureOwnerOrAdmin(comment, requesterId, isAdmin);

        long childCount = commentRepository.countByParentId(id);
        if (childCount == 0) {
            // 자식(대댓글)이 없으면 완전 삭제, 있으면 소프트 삭제 후 "삭제된 댓글" 로 표시한다.
            commentRepository.hardDelete(id);
        } else {
            commentRepository.softDelete(id);
        }
    }

    private void ensureOwnerOrAdmin(Comment comment, Long requesterId, boolean isAdmin) {
        if (isAdmin) {
            return;
        }
        if (requesterId == null || !requesterId.equals(comment.getAuthorId())) {
            throw new BusinessException(ErrorCode.COMMENT_FORBIDDEN);
        }
    }

    public List<Comment> listForAdmin(Long postId, int page, int size) {
        return commentRepository.findAllForAdmin(postId, page * size, size);
    }

    public long countForAdmin(Long postId) {
        return commentRepository.countForAdmin(postId);
    }

    /** 관리자 댓글 목록 화면에 표시할 게시글 제목을 postId 기준으로 일괄 조회한다. */
    public Map<Long, String> resolvePostTitles(List<Comment> comments) {
        Map<Long, String> titles = new HashMap<>();
        for (Comment comment : comments) {
            titles.computeIfAbsent(comment.getPostId(),
                    id -> postRepository.findById(id).map(post -> post.getTitle()).orElse("(삭제된 게시글)"));
        }
        return titles;
    }
}
