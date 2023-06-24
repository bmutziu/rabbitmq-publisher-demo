# RabbitMQ-publisher Demo project

This Spring Boot demo project is an example of implementing
reliable RabbitMQ publisher with several retry mechanisms.
See the details at this [article](https://developers.ascendcorp.com/reliable-publishing-to-rabbitmq-with-spring-amqp-d2f3e81275e7)

## Prerequisite
Run RabbitMQ server on you local machine at port 5672.

## How to test publishing message
Run the Spring Boot app and do a POST request as below example :
```
curl --location 'http://localhost:8080/rabbitmq-publisher-demo/v1/data' \
--header 'Content-Type: application/json' \
--data '{
    "id": "1234"
}'
```