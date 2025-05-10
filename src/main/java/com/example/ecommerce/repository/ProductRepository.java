package com.example.ecommerce.repository;

import com.example.ecommerce.model.Product;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// intialized product with different items for in memory use , findAll, findByID , save and delete for admin if needed in future
@Repository
public class ProductRepository {
    private final Map<Long, Product> products = new HashMap<>();

    // Initialize with some sample products
    public ProductRepository() {
        products.put(1L, new Product(1L, "Laptop", "High-end gaming laptop", new BigDecimal("1299.99")));
        products.put(2L, new Product(2L, "Smartphone", "Latest smartphone model", new BigDecimal("899.99")));
        products.put(3L, new Product(3L, "Headphones", "Noise-cancelling headphones", new BigDecimal("249.99")));
        products.put(4L, new Product(4L, "Smartwatch", "Fitness tracking smartwatch", new BigDecimal("199.99")));
        products.put(5L, new Product(5L, "Tablet", "10-inch tablet with stylus", new BigDecimal("499.99")));
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId((long) (products.size() + 1));
        }
        products.put(product.getId(), product);
        return product;
    }

    public void deleteById(Long id) {
        products.remove(id);
    }
}
