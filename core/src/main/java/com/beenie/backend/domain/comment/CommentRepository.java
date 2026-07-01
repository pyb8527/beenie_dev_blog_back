package com.beenie.backend.domain.comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    Comment save(Comment comment);

    Optional<Comment> findById(Long id);

    /** 게시글의 댓글 전체(대댓글 포함, 작성자 정보 join)를 등록순으로 조회한다. */
    List<Comment> findAllByPostId(Long postId);

    void softDelete(Long id);

    /** 완전 삭제 (자식 댓글이 없는 삭제된 댓글 정리용). */
    void hardDelete(Long id);

    long countByParentId(Long parentId);

    void softDeleteAllByPostId(Long postId);

    long countAll();

    /** 관리자용 댓글 목록. postId 가 null 이면 전체 댓글. */
    List<Comment> findAllForAdmin(Long postId, int offset, int limit);

    long countForAdmin(Long postId);
}
