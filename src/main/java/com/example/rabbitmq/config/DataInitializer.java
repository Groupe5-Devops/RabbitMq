package com.example.rabbitmq.config;

import com.example.rabbitmq.model.Product;
import com.example.rabbitmq.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final ProductRepository productRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (productRepository.count() == 0) {
                // Create iPhone Charger
                Product charger = new Product();
                charger.setName("iPhone Charger");
                charger.setDescription("Fast charging USB-C cable");
                charger.setPrice(new BigDecimal("29.99"));
                charger.setStockQuantity(100);
                charger.setSku("CHAR-001");
                productRepository.save(charger);
                log.info("Created product: {}", charger.getName());

                // Create Wireless Earbuds
                Product earbuds = new Product();
                earbuds.setName("Wireless Earbuds");
                earbuds.setDescription("Bluetooth 5.0 earphones");
                earbuds.setPrice(new BigDecimal("120.00"));
                earbuds.setStockQuantity(50);
                earbuds.setSku("EARBUD-002");
                productRepository.save(earbuds);
                log.info("Created product: {}", earbuds.getName());
            }
        };
    }
}
