package com.example.demo.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.queue.monitoring}")
    private String MONITORING_QUEUE;

    @Value("${rabbitmq.queue.measurements:queueMeasurements}")
    private String MEASUREMENTS_QUEUE;

    @Value("${rabbitmq.queue.devices.deletion}")
    private String DEVICES_DELETION_QUEUE;

    @Bean
    public Queue devicesDeletionQueue() {
        return new Queue(DEVICES_DELETION_QUEUE, true);
    }

    @Value("${rabbitmq.exchange.sync:sync.exchange}")
    private String SYNC_EXCHANGE;

    @Bean
    public Queue deviceCreatedQueue() {
        return new Queue(MONITORING_QUEUE, true);
    }

    @Bean
    public Queue measurementsQueue() {
        return new Queue(MEASUREMENTS_QUEUE, true);
    }

    @Bean
    public Queue notificationsQueue() {
        return new Queue("queueNotifications", true);
    }

    @Bean
    public TopicExchange syncExchange() {
        return new TopicExchange(SYNC_EXCHANGE);
    }

    @Bean
    public Binding monitoringBinding(
            Queue deviceCreatedQueue,
            TopicExchange syncExchange) {
        return BindingBuilder
                .bind(deviceCreatedQueue)
                .to(syncExchange)
                .with("device.created");
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
