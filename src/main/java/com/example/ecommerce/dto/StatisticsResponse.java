package com.example.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    private int totalItemsPurchased;
    private BigDecimal totalPurchaseAmount;
    private List<String> discountCodes;
    private BigDecimal totalDiscountAmount;
}
