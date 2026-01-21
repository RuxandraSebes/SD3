package com.example.simulator.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // CRITICAL FIX: Use the same queue name as Monitoring Service
    // Changed from "sensor-data-queue" to "queueMeasurements"
    @Value("${rabbitmq.queue.measurements:queueMeasurements}")
    private String MEASUREMENTS_QUEUE;

    @Bean
    public Queue measurementsQueue() {
        return new Queue(MEASUREMENTS_QUEUE, true); // durable queue
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}