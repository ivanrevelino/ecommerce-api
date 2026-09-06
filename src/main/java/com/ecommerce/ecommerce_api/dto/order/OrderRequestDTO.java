package com.ecommerce.ecommerce_api.dto.order;

import com.ecommerce.ecommerce_api.dto.order_item.OrderItemRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequestDTO(
        @NotEmpty(message = "Order items cannot be empty")
        List<@Valid OrderItemRequestDTO> items
) {
}
