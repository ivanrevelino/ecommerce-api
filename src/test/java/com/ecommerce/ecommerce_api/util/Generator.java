package com.ecommerce.ecommerce_api.util;

import com.ecommerce.ecommerce_api.dto.category.CategoryRequestDTO;
import com.ecommerce.ecommerce_api.dto.category.CategoryResponseDTO;
import com.ecommerce.ecommerce_api.dto.login_register.LoginRequestDTO;
import com.ecommerce.ecommerce_api.dto.login_register.LoginResponseDTO;
import com.ecommerce.ecommerce_api.dto.login_register.RegisterRequestDTO;
import com.ecommerce.ecommerce_api.dto.login_register.RegisterResponseDTO;
import com.ecommerce.ecommerce_api.dto.order.OrderRequestDTO;
import com.ecommerce.ecommerce_api.dto.order.OrderResponseDTO;
import com.ecommerce.ecommerce_api.dto.order_item.OrderItemRequestDTO;
import com.ecommerce.ecommerce_api.dto.order_item.OrderItemResponseDTO;
import com.ecommerce.ecommerce_api.dto.product.ProductRequestDTO;
import com.ecommerce.ecommerce_api.dto.product.ProductResponseDTO;
import com.ecommerce.ecommerce_api.dto.user.UserPasswordUpdateDTO;
import com.ecommerce.ecommerce_api.dto.user.UserResponseDTO;
import com.ecommerce.ecommerce_api.dto.user.UserUpdateDTO;
import com.ecommerce.ecommerce_api.models.Category;
import com.ecommerce.ecommerce_api.models.OrderItem;
import com.ecommerce.ecommerce_api.models.Product;
import com.ecommerce.ecommerce_api.models.User;
import com.ecommerce.ecommerce_api.models.enums.OrderStatus;
import com.ecommerce.ecommerce_api.models.enums.UserRoles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class Generator {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1, 1, 12, 0);

    private Generator() {
    }

    public static Category category() {
        return Category.builder().name("Food").build();
    }

    public static CategoryRequestDTO categoryRequest() {
        return new CategoryRequestDTO("Food");
    }

    public static CategoryResponseDTO categoryResponse() {
        return new CategoryResponseDTO(1L, "Food");
    }

    public static Product product() {
        return Product.builder()
                .name("Banana")
                .price(BigDecimal.TEN)
                .category(category())
                .stock(10)
                .build();
    }

    public static ProductRequestDTO productRequest() {
        return new ProductRequestDTO("Banana", BigDecimal.TEN, 1L, 10);
    }

    public static ProductResponseDTO productResponse() {
        return new ProductResponseDTO(1L, "Banana", BigDecimal.TEN, 1L, "Food", 10);
    }

    public static User user() {
        return User.builder()
                .id(1L)
                .username("test-user")
                .password("encoded-password")
                .roles(UserRoles.USER)
                .createdAt(CREATED_AT)
                .build();
    }

    public static UserResponseDTO userResponse() {
        return new UserResponseDTO(1L, "test-user", UserRoles.USER, CREATED_AT);
    }

    public static UserUpdateDTO userUpdate() {
        return new UserUpdateDTO("updated-user", UserRoles.ADMIN);
    }

    public static UserPasswordUpdateDTO userPasswordUpdate() {
        return new UserPasswordUpdateDTO("old-password", "new-password");
    }

    public static LoginRequestDTO loginRequest() {
        return new LoginRequestDTO("test-user", "password");
    }

    public static LoginResponseDTO loginResponse() {
        return new LoginResponseDTO("signed-token");
    }

    public static RegisterRequestDTO registerRequest() {
        return new RegisterRequestDTO("new-user", "password");
    }

    public static RegisterResponseDTO registerResponse() {
        return new RegisterResponseDTO(1L, "new-user", CREATED_AT, UserRoles.USER);
    }

    public static OrderItem orderItem() {
        return OrderItem.builder()
                .product(product())
                .price(BigDecimal.TEN)
                .quantity(2)
                .subtotal(BigDecimal.valueOf(20))
                .build();
    }

    public static OrderItemRequestDTO orderItemRequest() {
        return new OrderItemRequestDTO(1L, 2);
    }

    public static OrderItemResponseDTO orderItemResponse() {
        return new OrderItemResponseDTO(1L, 1L, "Banana", BigDecimal.TEN, 2, BigDecimal.valueOf(20));
    }

    public static OrderRequestDTO orderRequest() {
        return new OrderRequestDTO(List.of(orderItemRequest()));
    }

    public static OrderResponseDTO orderResponse() {
        return new OrderResponseDTO(
                1L,
                1L,
                "test-user",
                List.of(orderItemResponse()),
                BigDecimal.valueOf(20),
                OrderStatus.PENDING,
                CREATED_AT
        );
    }
}
