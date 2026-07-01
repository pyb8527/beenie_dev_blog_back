package com.beenie.backend.domain.event;

/**
 * 게시글 조회수/변경 이벤트 발행 Port (RabbitMQ 구현).
 */
public interface PostEventPublisher {

    /** 조회수 반영 비동기 이벤트 발행. */
    void publishViewIncrement(Long postId);

    /** 게시글 생성/수정/삭제 시 Sitemap/RSS 캐시 재생성 트리거. */
    void publishPostChanged();
}
