package com.example.ecommerce.service;

import com.example.ecommerce.dto.CheckoutRequest;
import com.example.ecommerce.dto.StatisticsResponse;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private DiscountService discountService;

    @InjectMocks
    private OrderService orderService;

    private static final String USER_ID = "user123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckout_WithoutDiscount() {
        // Arrange
        CartItem item = new CartItem(1L, "Test Product", 2, new BigDecimal("10.00"));
        List<CartItem> items = Arrays.asList(item);
        
        Cart cart = new Cart(USER_ID, items);
        
        CheckoutRequest request = new CheckoutRequest();
        request.setUserId(USER_ID);
        request.setDiscountCode(null);
        
        when(cartService.getCart(USER_ID)).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);
            return savedOrder;
        });

        // Act
        Order result = orderService.checkout(request);

        // Assert
        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        assertEquals(1, result.getItems().size());
        assertEquals(new BigDecimal("20.00"), result.getTotalAmount());
        assertEquals(BigDecimal.ZERO, result.getDiscountAmount());
        assertEquals(new BigDecimal("20.00"), result.getFinalAmount());
        assertNull(result.getDiscountCode());
        
        verify(cartService).clearCart(USER_ID);
        verify(discountService).isEligibleForDiscount();
    }

    @Test
    void testCheckout_WithDiscount() {
        // Arrange
        String discountCode = "DISCOUNT-12345";
        BigDecimal discount = new BigDecimal("2.00");
        
        CartItem item = new CartItem(1L, "Test Product", 2, new BigDecimal("10.00"));
        List<CartItem> items = Arrays.asList(item);
        
        Cart cart = new Cart(USER_ID, items);
        
        CheckoutRequest request = new CheckoutRequest();
        request.setUserId(USER_ID);
        request.setDiscountCode(discountCode);
        
        when(cartService.getCart(USER_ID)).thenReturn(cart);
        when(discountService.validateDiscountCode(discountCode)).thenReturn(true);
        when(discountService.calculateDiscount(any(BigDecimal.class))).thenReturn(discount);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);
            return savedOrder;
        });

        // Act
        Order result = orderService.checkout(request);

        // Assert
        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        assertEquals(1, result.getItems().size());
        assertEquals(new BigDecimal("20.00"), result.getTotalAmount());
        assertEquals(discount, result.getDiscountAmount());
        assertEquals(new BigDecimal("18.00"), result.getFinalAmount());
        assertEquals(discountCode, result.getDiscountCode());
        
        verify(cartService).clearCart(USER_ID);
        verify(discountService).markDiscountCodeAsUsed(discountCode, 1L);
        verify(discountService).isEligibleForDiscount();
    }

    @Test
    void testGetStatistics() {
        // Arrange
        CartItem item1 = new CartItem(1L, "Product 1", 2, BigDecimal.TEN);
        CartItem item2 = new CartItem(2L, "Product 2", 1, BigDecimal.TEN);
        
        List<Order> orders = Arrays.asList(
                new Order(1L, "user1", Arrays.asList(item1), new BigDecimal("20"), null, BigDecimal.ZERO, new BigDecimal("20"), LocalDateTime.now()),
                new Order(2L, "user2", Arrays.asList(item2), new BigDecimal("10"), "DISCOUNT", new BigDecimal("1"), new BigDecimal("9"), LocalDateTime.now())
        );
        
        List<String> discountCodes = Arrays.asList("DISCOUNT-1", "DISCOUNT-2");
        
        when(orderRepository.findAll()).thenReturn(orders);
        when(discountService.getAllDiscountCodeValues()).thenReturn(discountCodes);

        // Act
        StatisticsResponse result = orderService.getStatistics();

        // Assert
        assertEquals(3, result.getTotalItemsPurchased()); // 2 + 1 = 3
        assertEquals(new BigDecimal("30"), result.getTotalPurchaseAmount()); // 20 + 10 = 30
        assertEquals(discountCodes, result.getDiscountCodes());
        assertEquals(new BigDecimal("1"), result.getTotalDiscountAmount()); // 0 + 1 = 1
    }
}
