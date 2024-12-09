package com.example.rabbitmq.model;

public enum OrderStatus {
    PENDING,
    PROCESSING,
    VALIDATING,
    PAYMENT_PENDING,
    PAYMENT_PROCESSING,
    PAYMENT_FAILED,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED,
    ERROR,
    RETRY_1,
    RETRY_2,
    RETRY_3
}
