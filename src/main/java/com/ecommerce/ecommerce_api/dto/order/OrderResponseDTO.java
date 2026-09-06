package com.ecommerce.ecommerce_api.dto.order;

import com.ecommerce.ecommerce_api.dto.order_item.OrderItemResponseDTO;
import com.ecommerce.ecommerce_api.models.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        Long userId,
        String username,
        List<OrderItemResponseDTO> items,
        BigDecimal total,
        OrderStatus status,
        LocalDateTime createdAt
) {
}
