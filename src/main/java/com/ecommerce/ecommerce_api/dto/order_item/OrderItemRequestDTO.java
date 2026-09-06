package com.ecommerce.ecommerce_api.dto.order_item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDTO(
        @NotNull(message = "Product id cannot be null")
        Long productId,

        @NotNull(message = "Product quantity cannot be null")
        @Min(value = 1, message = "Product quantity must be greater than zero")
        Integer quantity
) {
}
