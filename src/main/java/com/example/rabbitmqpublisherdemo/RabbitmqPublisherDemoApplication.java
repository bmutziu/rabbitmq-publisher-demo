package com.example.rabbitmqpublisherdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RabbitmqPublisherDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitmqPublisherDemoApplication.class, args);
	}

}
