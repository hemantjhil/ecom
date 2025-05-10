package com.example.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    
    // Default: Every 5th order gets a discount
    @Value("${ecommerce.discount.nth-order:5}")
    private int nthOrderForDiscount;
    
    // Default: 10% discount
    @Value("${ecommerce.discount.percentage:10}")
    private int discountPercentage;
    
    public int getNthOrderForDiscount() {
        return nthOrderForDiscount;
    }
    
    public int getDiscountPercentage() {
        return discountPercentage;
    }
}
