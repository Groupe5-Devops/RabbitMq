package com.example.rabbitmq.service;

import com.example.rabbitmq.model.Order;
import com.example.rabbitmq.model.OrderStatus;
import com.example.rabbitmq.model.Payment;
import com.example.rabbitmq.model.PaymentStatus;
import com.example.rabbitmq.model.Product;
import com.example.rabbitmq.repository.OrderRepository;
import com.example.rabbitmq.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void processOrder(Order order) {
        try {
            log.info("Processing order: {}", order.getOrderNumber());
            
            // Validate order
            validateOrder(order);
            
            // Calculate total amount
            order.calculateTotalAmount();
            
            // Process payment
            if (processPayment(order)) {
                order.setStatus(OrderStatus.PAID);
                saveOrder(order);
                log.info("Order processed successfully: {}", order.getOrderNumber());
            } else {
                order.setStatus(OrderStatus.PAYMENT_FAILED);
                saveOrder(order);
                log.error("Payment failed for order: {}", order.getOrderNumber());
                throw new RuntimeException("Payment processing failed");
            }
        } catch (Exception e) {
            log.error("Error processing order: {}", e.getMessage());
            order.setStatus(OrderStatus.ERROR);
            saveOrder(order);
            throw new RuntimeException("Order processing failed", e);
        }
    }

    private void validateOrder(Order order) {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        if (order.getPayment() == null) {
            throw new IllegalArgumentException("Order must have payment information");
        }
        
        // Validate products exist and have sufficient stock
        order.getOrderItems().forEach(item -> {
            Product product = productRepository.findById(item.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProduct().getId()));
            
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException(
                    String.format("Insufficient stock for product %s. Requested: %d, Available: %d",
                        product.getName(), item.getQuantity(), product.getStockQuantity())
                );
            }
            
            // Update product reference with the one from database
            item.setProduct(product);
            
            // Set the correct price from the database
            item.setPrice(product.getPrice());
        });
    }

    @Transactional
    public void saveOrder(Order order) {
        order.setOrderDate(LocalDateTime.now());
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }
        orderRepository.save(order);
        log.info("Order saved successfully: {}", order);
    }

    public boolean processPayment(Order order) {
        Payment payment = order.getPayment();
        
        try {
            // Validate payment amount
            if (payment.getAmount().compareTo(order.getTotalAmount()) != 0) {
                log.error("Payment amount {} doesn't match order total {}", 
                    payment.getAmount(), order.getTotalAmount());
                payment.setStatus(PaymentStatus.FAILED);
                return false;
            }

            // Simulate payment processing
            Thread.sleep(1000);
            
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
            return true;
        } catch (Exception e) {
            log.error("Payment processing failed: {}", e.getMessage());
            payment.setStatus(PaymentStatus.FAILED);
            return false;
        }
    }
}
