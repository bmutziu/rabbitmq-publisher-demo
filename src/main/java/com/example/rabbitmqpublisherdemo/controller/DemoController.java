package com.example.rabbitmqpublisherdemo.controller;

import com.example.rabbitmqpublisherdemo.model.CommonResponse;
import com.example.rabbitmqpublisherdemo.model.MyData;
import com.example.rabbitmqpublisherdemo.service.RabbitPublisherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1")
public class DemoController {

    private final RabbitPublisherService rabbitPublisherService;

    public DemoController(RabbitPublisherService rabbitPublisherService) {
        this.rabbitPublisherService = rabbitPublisherService;
    }

    @PostMapping("data")
    public ResponseEntity<CommonResponse> receiveData(@RequestBody MyData myData) {

        rabbitPublisherService.publishMessage(myData, 0);

        return ResponseEntity.ok(CommonResponse.genSuccessResponse());

    }

}
