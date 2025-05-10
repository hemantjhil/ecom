package com.example.ecommerce.repository;

import com.example.ecommerce.model.Cart;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
// Repo to save, delete and find cart by user ID
public class CartRepository {
    private final Map<String, Cart> carts = new HashMap<>();

    public Cart save(Cart cart) {
        carts.put(cart.getUserId(), cart);
        return cart;
    }

    public Optional<Cart> findByUserId(String userId) {
        return Optional.ofNullable(carts.get(userId));
    }

    public void deleteByUserId(String userId) {
        carts.remove(userId);
    }
}
