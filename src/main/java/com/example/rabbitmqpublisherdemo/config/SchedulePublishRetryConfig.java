package com.example.rabbitmqpublisherdemo.config;

import com.example.rabbitmqpublisherdemo.model.MyData;
import com.example.rabbitmqpublisherdemo.model.RetryCorrelationData;
import com.example.rabbitmqpublisherdemo.service.RabbitPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collection;
import java.util.Queue;

@Slf4j
@Configuration
public class SchedulePublishRetryConfig {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitPublisherService rabbitPublisherService;

    public SchedulePublishRetryConfig(RabbitTemplate rabbitTemplate, RabbitPublisherService rabbitPublisherService) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitPublisherService = rabbitPublisherService;
    }

    @Scheduled(fixedDelay = 10000)
    public void scheduleNackedRepublishTask() {
        Queue<RetryCorrelationData> queue = rabbitPublisherService.getNegativeAckedMessages();

        for (int i = 0; i < 100; i++) {
            if (queue.isEmpty()) {
                break;
            }
            RetryCorrelationData retryCorrelationData = queue.remove();

            log.info("Retry nack-ed message : {}", retryCorrelationData.getId());
            checkRetryCountAndRepublish(retryCorrelationData.getRetryCount(), retryCorrelationData.getId());
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void scheduleUnconfirmedRepublishTask() {
        Collection<CorrelationData> unconfirmed = rabbitTemplate.getUnconfirmed(10000);

        if (unconfirmed != null) {
            for (CorrelationData correlationData : unconfirmed) {
                RetryCorrelationData retryCorrelationData = (RetryCorrelationData) correlationData;

                log.info("Retry unconfirmed message id : {}", retryCorrelationData.getId());
                checkRetryCountAndRepublish(retryCorrelationData.getRetryCount(), correlationData.getId());
            }
        }
    }

    void checkRetryCountAndRepublish(int retryCount, String id) {
        MyData data = rabbitPublisherService.cleanOutstandingConfirm(id);

        if (data == null) {
            log.warn("Fail to retrieve data from outstandingConfirms queue with id : {}", id);
            return;
        }

        if (retryCount < 100) {
            rabbitPublisherService.publishMessage(data, retryCount + 1);
        } else {
            log.warn("Retry counter reach limit, will stop retry for this message");
        }
    }


}
