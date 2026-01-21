package com.example.demo.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Value("${rabbitmq.queue.people}")
    private String PEOPLE_QUEUE;

    @Value("${rabbitmq.queue.device}")
    private String DEVICE_QUEUE;

    @Value("${rabbitmq.queue.auth.deletion}")
    private String AUTH_DELETION_QUEUE;

    @Bean
    public Queue userQueue() {
        return new Queue(PEOPLE_QUEUE, true);
    }

    @Bean
    public Queue deviceQueue() {
        return new Queue(DEVICE_QUEUE, true);
    }

    @Bean
    public Queue authDeletionQueue() {
        return new Queue(AUTH_DELETION_QUEUE, true);
    }

    @Value("${rabbitmq.exchange.sync:sync.exchange}")
    private String SYNC_EXCHANGE;

    @Bean
    public TopicExchange syncExchange() {
        return new TopicExchange(SYNC_EXCHANGE);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }
}