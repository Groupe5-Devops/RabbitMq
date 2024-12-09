package com.example.rabbitmq.listener;

import com.example.rabbitmq.model.Order;
import com.example.rabbitmq.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMessageListener {

    private final OrderService orderService;

    @RabbitListener(queues = "${rabbitmq.queue.name}", containerFactory = "rabbitListenerContainerFactory")
    public void handleOrderMessage(Order order, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            log.info("Received order message: {}", order);
            orderService.processOrder(order);
            channel.basicAck(tag, false);
            log.info("Order processed successfully: {}", order.getOrderNumber());
        } catch (Exception e) {
            try {
                log.error("Error processing order: {}", e.getMessage(), e);
                channel.basicNack(tag, false, false);
            } catch (Exception ex) {
                log.error("Error sending NACK: {}", ex.getMessage(), ex);
            }
        }
    }
}
