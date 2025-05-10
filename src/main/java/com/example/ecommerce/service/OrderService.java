package com.example.ecommerce.service;

import com.example.ecommerce.dto.CheckoutRequest;
import com.example.ecommerce.dto.StatisticsResponse;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.DiscountCode;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final DiscountService discountService;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            CartService cartService,
            DiscountService discountService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.discountService = discountService;
    }

    public Order checkout(CheckoutRequest request) {
        String userId = request.getUserId();
        String discountCode = request.getDiscountCode();
        
        // Get user's cart
        Cart cart = cartService.getCart(userId);
        
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        
        // Calculate total amount
        BigDecimal totalAmount = cart.getTotal();
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = totalAmount;
        
        // Apply discount if code is valid
        if (discountCode != null && !discountCode.isEmpty()) {
            boolean isValid = discountService.validateDiscountCode(discountCode);
            
            if (isValid) {
                discountAmount = discountService.calculateDiscount(totalAmount);
                finalAmount = totalAmount.subtract(discountAmount);
            } else {
                throw new NoSuchElementException("Invalid discount code: " + discountCode);
            }
        }
        
        // Create and save order
        Order order = new Order();
        order.setUserId(userId);
        order.setItems(cart.getItems());
        order.setTotalAmount(totalAmount);
        order.setDiscountCode(discountCode);
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(finalAmount);
        order.setCreatedAt(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);
        
        // Mark discount code as used if applied
        if (discountCode != null && !discountCode.isEmpty()) {
            discountService.markDiscountCodeAsUsed(discountCode, savedOrder.getId());
        }
        
        // Clear the cart after successful checkout
        cartService.clearCart(userId);
        
        // Generate new discount code if this order is the nth order
        if (discountService.isEligibleForDiscount()) {
            discountService.generateDiscountCode();
        }
        
        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + id));
    }

    public StatisticsResponse getStatistics() {
        List<Order> orders = orderRepository.findAll();
        
        // Calculate total items purchased
        int totalItemsPurchased = orders.stream()
                .flatMap(order -> order.getItems().stream())
                .mapToInt(item -> item.getQuantity())
                .sum();
        
        // Calculate total purchase amount
        BigDecimal totalPurchaseAmount = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Get all discount codes
        List<String> discountCodes = discountService.getAllDiscountCodeValues();
        
        // Calculate total discount amount
        BigDecimal totalDiscountAmount = orders.stream()
                .map(Order::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new StatisticsResponse(
                totalItemsPurchased,
                totalPurchaseAmount,
                discountCodes,
                totalDiscountAmount
        );
    }
}
