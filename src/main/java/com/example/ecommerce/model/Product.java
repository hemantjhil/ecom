package com.example.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
// product model to identify each product with basic parameter like id, name, description and price
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
}
