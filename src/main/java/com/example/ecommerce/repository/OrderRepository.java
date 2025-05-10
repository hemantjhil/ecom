package com.example.ecommerce.repository;

import com.example.ecommerce.model.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

// repo to save order, find order by order id, checking all order for admin and finding the order size
@Repository
public class OrderRepository {
    private final Map<Long, Order> orders = new HashMap<>();
    private final AtomicLong orderIdSequence = new AtomicLong(1);

    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(orderIdSequence.getAndIncrement());
        }
        orders.put(order.getId(), order);
        return order;
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    public int count() {
        return orders.size();
    }
}
