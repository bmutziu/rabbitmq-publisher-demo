package com.example.rabbitmqpublisherdemo.service;

import com.example.rabbitmqpublisherdemo.model.MyData;
import com.example.rabbitmqpublisherdemo.model.RetryCorrelationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Slf4j
public class RabbitPublisherService {

    private final RabbitTemplate rabbitTemplate;
    protected ConcurrentHashMap<String, MyData> outstandingConfirms = new ConcurrentHashMap<>();
    protected ConcurrentLinkedQueue<RetryCorrelationData> negativeAckedMessages = new ConcurrentLinkedQueue<>();

    public RabbitPublisherService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    public void postConstruct() {
        rabbitTemplate.setConfirmCallback((correlation, ack, reason) -> {

            if (correlation == null) {
                return;
            }

            RetryCorrelationData retryCorrelationData = (RetryCorrelationData) correlation;
            handleRabbitAcknowledgement(ack, retryCorrelationData);
        });
    }

    void handleRabbitAcknowledgement(boolean isAck, RetryCorrelationData retryCorrelationData) {
        final String id = retryCorrelationData.getId();
        if (!isAck) {
            log.info("id {} got nack-ed, save message to nack-ed message queue for retry", id);
            negativeAckedMessages.add(new RetryCorrelationData(id,
                    retryCorrelationData.getRetryCount() + 1));
        } else {
            // acked, message reached the broker successsfully
            cleanOutstandingConfirm(id);
            log.info("id {} got acked", id);
        }
    }

    public void publishMessage(MyData myData, int retryCount) {
        final String id = myData.getId();

        RetryCorrelationData correlationData = new RetryCorrelationData(id, retryCount);

        outstandingConfirms.putIfAbsent(id, myData);
        rabbitTemplate.convertAndSend("demo-publisher-exchange", "demo-publisher.create",
                myData, correlationData);
    }

    public MyData cleanOutstandingConfirm(String id) {
        // remove the data from outstandingConfirms
        // created as public method for usage in other retry schedulers as well.
        return outstandingConfirms.remove(id);
    }

    public Queue<RetryCorrelationData> getNegativeAckedMessages() {
        return negativeAckedMessages;
    }

}
