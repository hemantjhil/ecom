package com.example.ecommerce.service;

import com.example.ecommerce.dto.AddToCartRequest;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartService cartService;

    private static final String USER_ID = "user123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCartWhenCartExists() {
        // Arrange
        Cart expectedCart = new Cart(USER_ID, new ArrayList<>());
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(expectedCart));

        // Act
        Cart result = cartService.getCart(USER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        verify(cartRepository).findByUserId(USER_ID);
    }

    @Test
    void testGetCartWhenCartDoesNotExist() {
        // Arrange
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Cart result = cartService.getCart(USER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        assertEquals(0, result.getItems().size());
    }

    @Test
    void testAddToCartNewItem() {
        // Arrange
        Cart cart = new Cart(USER_ID, new ArrayList<>());
        Product product = new Product(1L, "Test Product", "Description", new BigDecimal("10.00"));
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(productService.getProductById(1L)).thenReturn(product);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Cart result = cartService.addToCart(USER_ID, request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        CartItem addedItem = result.getItems().get(0);
        assertEquals(1L, addedItem.getProductId());
        assertEquals(2, addedItem.getQuantity());
        assertEquals(new BigDecimal("10.00"), addedItem.getUnitPrice());
        assertEquals(new BigDecimal("20.00"), addedItem.getTotalPrice());
    }

    @Test
    void testAddToCartExistingItem() {
        // Arrange
        CartItem existingItem = new CartItem(1L, "Test Product", 1, new BigDecimal("10.00"));
        Cart cart = new Cart(USER_ID, new ArrayList<>());
        cart.getItems().add(existingItem);

        Product product = new Product(1L, "Test Product", "Description", new BigDecimal("10.00"));
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(productService.getProductById(1L)).thenReturn(product);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Cart result = cartService.addToCart(USER_ID, request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        CartItem updatedItem = result.getItems().get(0);
        assertEquals(1L, updatedItem.getProductId());
        assertEquals(3, updatedItem.getQuantity()); // 1 + 2 = 3
        assertEquals(new BigDecimal("10.00"), updatedItem.getUnitPrice());
        assertEquals(new BigDecimal("30.00"), updatedItem.getTotalPrice());
    }

    @Test
    void testRemoveFromCart() {
        // Arrange
        CartItem item1 = new CartItem(1L, "Product 1", 1, new BigDecimal("10.00"));
        CartItem item2 = new CartItem(2L, "Product 2", 1, new BigDecimal("20.00"));
        
        Cart cart = new Cart(USER_ID, new ArrayList<>());
        cart.getItems().add(item1);
        cart.getItems().add(item2);

        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Cart result = cartService.removeFromCart(USER_ID, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(2L, result.getItems().get(0).getProductId());
        verify(cartRepository).save(cart);
    }

    @Test
    void testClearCart() {
        // Act
        cartService.clearCart(USER_ID);

        // Assert
        verify(cartRepository).deleteByUserId(USER_ID);
    }
}
