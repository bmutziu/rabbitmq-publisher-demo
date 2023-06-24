package com.example.rabbitmqpublisherdemo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class SpringRabbitMqConfig {

    @Bean
    DirectExchange exchange() {
        return new DirectExchange("demo-publisher-exchange");
    }

    @Bean
    Queue testQueue() {
        return new Queue("test-queue", true);
    }

    @Bean
    Binding bindingTestQueue(Queue testQueue, DirectExchange exchange) {
        return BindingBuilder.bind(testQueue).to(exchange).with("demo-publisher.create");
    }


    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        backOffPolicy.setMultiplier(3.0);
        backOffPolicy.setMaxInterval(3000_000);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(15);

        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        rabbitTemplate.setRetryTemplate(retryTemplate);

        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }

}
