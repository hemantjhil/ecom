package com.example.ecommerce.service;

import com.example.ecommerce.config.ApplicationConfig;
import com.example.ecommerce.model.DiscountCode;
import com.example.ecommerce.repository.DiscountCodeRepository;
import com.example.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DiscountService {
    private final DiscountCodeRepository discountCodeRepository;
    private final OrderRepository orderRepository;
    private final ApplicationConfig applicationConfig;

    @Autowired
    public DiscountService(
            DiscountCodeRepository discountCodeRepository,
            OrderRepository orderRepository,
            ApplicationConfig applicationConfig) {
        this.discountCodeRepository = discountCodeRepository;
        this.orderRepository = orderRepository;
        this.applicationConfig = applicationConfig;
    }

    public DiscountCode generateDiscountCode() {
        // Generate a random discount code
        String code = "DISCOUNT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        DiscountCode discountCode = new DiscountCode(code, false, null);
        return discountCodeRepository.save(discountCode);
    }

    public boolean isEligibleForDiscount() {
        int nthOrder = applicationConfig.getNthOrderForDiscount();
        int totalOrders = orderRepository.count();
        
        // If total orders is divisible by nth order value, it's eligible
        return totalOrders > 0 && totalOrders % nthOrder == 0;
    }

    public BigDecimal calculateDiscount(BigDecimal amount) { // providing doscount percentage with 2 decimal places
        int discountPercentage = applicationConfig.getDiscountPercentage();
        return amount.multiply(BigDecimal.valueOf(discountPercentage))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public boolean validateDiscountCode(String code) { //validating discount by checking from DB and is not used
        if (code == null || code.isEmpty()) {
            return false;
        }
        
        DiscountCode discountCode = discountCodeRepository.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException("Discount code not found: " + code));
        
        return !discountCode.isUsed();
    }

    public void markDiscountCodeAsUsed(String code, Long orderId) {
        DiscountCode discountCode = discountCodeRepository.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException("Discount code not found: " + code));
        
        discountCode.setUsed(true);
        discountCode.setOrderId(orderId);
        
        discountCodeRepository.save(discountCode);
    }

    public List<DiscountCode> getAllDiscountCodes() {
        return discountCodeRepository.findAll();
    }

    public List<String> getAllDiscountCodeValues() {
        return discountCodeRepository.findAll().stream()
                .map(DiscountCode::getCode)
                .collect(Collectors.toList());
    }
}
