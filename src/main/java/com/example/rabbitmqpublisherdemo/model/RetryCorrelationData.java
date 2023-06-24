package com.example.rabbitmqpublisherdemo.model;

import org.springframework.amqp.rabbit.connection.CorrelationData;

public class RetryCorrelationData extends CorrelationData {

    private final int retryCount;

    public RetryCorrelationData(String id, int retryCount) {
        super(id);
        this.retryCount = retryCount;
    }

    public int getRetryCount() {
        return retryCount;
    }
}