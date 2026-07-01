package com.beenie.backend.storage.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "blog-events-exchange";

    public static final String VIEW_COUNT_QUEUE = "view-count-queue";
    public static final String VIEW_COUNT_ROUTING_KEY = "view-count";

    public static final String POST_CHANGED_QUEUE = "post-changed-queue";
    public static final String POST_CHANGED_ROUTING_KEY = "post-changed";

    @Bean
    public TopicExchange blogEventsExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue viewCountQueue() {
        return new Queue(VIEW_COUNT_QUEUE, true);
    }

    @Bean
    public Binding viewCountBinding() {
        return BindingBuilder.bind(viewCountQueue()).to(blogEventsExchange()).with(VIEW_COUNT_ROUTING_KEY);
    }

    @Bean
    public Queue postChangedQueue() {
        return new Queue(POST_CHANGED_QUEUE, true);
    }

    @Bean
    public Binding postChangedBinding() {
        return BindingBuilder.bind(postChangedQueue()).to(blogEventsExchange()).with(POST_CHANGED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
