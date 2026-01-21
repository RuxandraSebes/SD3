package com.example.demo.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.queue.device}")
    private String DEVICE_QUEUE;

    @Value("${rabbitmq.queue.monitoring}")
    private String MONITORING_QUEUE;

    @Value("${rabbitmq.queue.devices.deletion}")
    private String DEVICES_DELETION_QUEUE;

    public static final String SYNC_EXCHANGE = "sync.exchange"; // <-- NOU

    @Bean // <-- NOU
    public TopicExchange syncExchange() {
        return new TopicExchange(SYNC_EXCHANGE);
    }

    @Bean
    public Queue deviceQueue() {
        return new Queue(DEVICE_QUEUE, true);
    }

    @Bean
    public Queue monitoringQueue() {
        return new Queue(MONITORING_QUEUE, true);
    }

    @Value("${rabbitmq.queue.user.sync:queuePeopleSync}")
    private String USER_SYNC_QUEUE;

    @Bean
    public Queue userSyncQueue() {
        return new Queue(USER_SYNC_QUEUE, true);
    }

    @Bean
    public Binding userSyncBinding(Queue userSyncQueue, TopicExchange syncExchange) {
        return BindingBuilder.bind(userSyncQueue).to(syncExchange).with("#"); // Bind with wildcard to receive all
    }

    @Bean
    public Queue deleteDeviceQueue() {
        return new Queue(DEVICES_DELETION_QUEUE, true);
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
}