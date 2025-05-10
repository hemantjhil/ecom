package com.example.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
// model to identify each item of Cart with their parameters like id of product, product name, stock ,each unit price and
// total price of n items of same type
public class CartItem {
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    
    // Calculate total price for this item
    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
