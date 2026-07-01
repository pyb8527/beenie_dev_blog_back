package com.beenie.backend.infrastructure.messaging;

import com.beenie.backend.domain.post.PostRepository;
import com.beenie.backend.storage.rabbitmq.RabbitMqConfig;
import com.beenie.backend.storage.rabbitmq.ViewCountMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 조회수 증가 이벤트를 소비하여 DB(posts.view_count)에 비동기로 반영한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountConsumer {

    private final PostRepository postRepository;

    @RabbitListener(queues = RabbitMqConfig.VIEW_COUNT_QUEUE)
    public void onViewCountMessage(ViewCountMessage message) {
        try {
            postRepository.incrementViewCount(message.postId());
        } catch (Exception e) {
            log.error("조회수 반영 실패 postId={}", message.postId(), e);
        }
    }
}
