package com.beenie.backend.infrastructure.messaging;

import com.beenie.backend.domain.event.PostEventPublisher;
import com.beenie.backend.storage.rabbitmq.PostEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostEventPublisherAdapter implements PostEventPublisher {

    private final PostEventProducer postEventProducer;

    @Override
    public void publishViewIncrement(Long postId) {
        postEventProducer.publishViewIncrement(postId);
    }

    @Override
    public void publishPostChanged() {
        postEventProducer.publishPostChanged();
    }
}
