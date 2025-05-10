# E-Commerce API with Discount Functionality

A Spring Boot application that provides APIs for an e-commerce store with cart and checkout functionality, including discount code generation and validation. Every nth order gets a coupon code for 10% discount which can be applied to their cart.

## Features

- Add items to cart
- Remove items from cart
- Checkout functionality with discount code validation
- Discount code generation for every nth order (configurable)
- Admin APIs for statistics and discount code management
- Swagger UI implementation to expose and try APIs

## Technology Stack

- Spring Boot 2.7.1

## Project Structure

```
src/main/java/com/example/ecommerce/
├── EcommerceApplication.java              # Main application class
├── config/                                # Configuration classes
│   ├── ApplicationConfig.java             # Application-specific configuration
|   ├── OpenApiConfig.java                 # Swagger/OpenAPI configuration
│   └── WebConfig.java                     # Web configuration (CORS, etc.)
├── controller/                            # REST controllers
│   ├── AdminController.java               # Admin APIs
│   ├── CartController.java                # Cart management APIs
│   ├── CheckoutController.java            # Checkout process API
│   ├── GlobalExceptionHandler.java        # Exception handling
│   ├── ProductController.java             # Product APIs
│   └── WebController.java                 # HTML page routing
├── dto/                                   # Data Transfer Objects
│   ├── AddToCartRequest.java              # Request for adding to cart
│   ├── CheckoutRequest.java               # Request for checkout
│   └── StatisticsResponse.java            # Response for statistics API
├── model/                                 # Domain models
│   ├── Cart.java                          # Shopping cart
│   ├── CartItem.java                      # Item in cart
│   ├── DiscountCode.java                  # Discount code
│   ├── Order.java                         # Order details
│   └── Product.java                       # Product information
├── repository/                            # Data repositories
│   ├── CartRepository.java                # In-memory cart storage
│   ├── DiscountCodeRepository.java        # In-memory discount codes storage
│   ├── OrderRepository.java               # In-memory order storage
│   └── ProductRepository.java             # In-memory product storage
└── service/                               # Business logic
    ├── CartService.java                   # Cart management
    ├── DiscountService.java               # Discount code operations
    ├── OrderService.java                  # Order processing
    └── ProductService.java                # Product operations
```

## API Endpoints

### Customer APIs

- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/cart?userId={userId}` - Get user's cart
- `POST /api/cart/add?userId={userId}` - Add item to cart
- `DELETE /api/cart/remove/{productId}?userId={userId}` - Remove item from cart
- `POST /api/checkout` - Process checkout with optional discount code

### Admin APIs

- `GET /api/admin/generate-discount` - Generate a discount code
- `GET /api/admin/statistics` - Get order statistics

## Setup and Running the Application

## Configuration

The following properties can be configured in `application.properties`:

```properties
# Default: Every 5th order gets a discount
ecommerce.discount.nth-order=5

# Default: 10% discount
ecommerce.discount.percentage=10
```

## Testing

Run unit tests with Maven:

```bash
mvn test
```

## Using the Application

1. Open `http://localhost:8080/swagger-ui/index.html` in your browser
2. Check different APIs of User and Admin by trying input to it


## Assumptions Made

- The system uses an in-memory data store, so data is lost when the application restarts
- A simple user identification system is used (random user IDs for demonstration)
- Discount codes can only be used once
- Discount codes apply to the entire order (not individual items)

## Potential Improvements

1. Add user authentication and authorization
2. Implement a persistent database backend
