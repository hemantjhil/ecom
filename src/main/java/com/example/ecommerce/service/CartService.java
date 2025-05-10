package com.example.ecommerce.service;

import com.example.ecommerce.dto.AddToCartRequest;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;

    @Autowired
    public CartService(CartRepository cartRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    public Cart getCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return newCart;
                });
    }

    public Cart addToCart(String userId, AddToCartRequest request) {
        Cart cart = getCart(userId);
        
        // Get product details
        Product product = productService.getProductById(request.getProductId());
        
        // Check if product already in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();
        
        if (existingItem.isPresent()) {
            // Update quantity if product already in cart
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            // Add new item to cart
            CartItem newItem = new CartItem(
                    product.getId(),
                    product.getName(),
                    request.getQuantity(),
                    product.getPrice()
            );
            cart.getItems().add(newItem);
        }
        
        return cartRepository.save(cart);
    }

    public Cart removeFromCart(String userId, Long productId) {
        Cart cart = getCart(userId);
        
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        
        return cartRepository.save(cart);
    }

    public void clearCart(String userId) {
        cartRepository.deleteByUserId(userId);
    }
}
