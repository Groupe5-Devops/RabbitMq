package com.example.rabbitmq.controller;

import com.example.rabbitmq.model.Order;
import com.example.rabbitmq.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final RabbitTemplate rabbitTemplate;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Order order) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, order);
            return ResponseEntity.ok("Order sent to processing queue");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Failed to process order: " + e.getMessage());
        }
    }
}
