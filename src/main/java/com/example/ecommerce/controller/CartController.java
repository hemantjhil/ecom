package com.example.ecommerce.controller;

import com.example.ecommerce.dto.AddToCartRequest;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Cart> getCart(@RequestParam String userId) { // finding the cart items for given user id
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/add") // adding item to the cart for given user id, product id and quantity
    public ResponseEntity<Cart> addToCart(
            @RequestParam String userId,
            @Valid @RequestBody AddToCartRequest request) {
        try {
            Cart updatedCart = cartService.addToCart(userId, request);
            return ResponseEntity.ok(updatedCart);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/remove/{productId}") // removing product by product id from the cart of given user
    public ResponseEntity<Cart> removeFromCart(
            @RequestParam String userId,
            @PathVariable Long productId) {
        try {
            Cart updatedCart = cartService.removeFromCart(userId, productId);
            return ResponseEntity.ok(updatedCart);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
