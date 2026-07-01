package com.beenie.backend.storage.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishViewIncrement(Long postId) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.VIEW_COUNT_ROUTING_KEY,
                new ViewCountMessage(postId));
    }

    public void publishPostChanged() {
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.POST_CHANGED_ROUTING_KEY, "changed");
    }
}
