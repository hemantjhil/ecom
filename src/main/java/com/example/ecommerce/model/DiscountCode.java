package com.example.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// model for discount code with their code, is_used and setting to order ID if applied to any order
public class DiscountCode {
    private String code;
    private boolean used;
    private Long orderId;
}
