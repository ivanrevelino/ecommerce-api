package com.ecommerce.ecommerce_api.dto.order_item;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        Long id,
        Long productId,
        String productName,
        BigDecimal price,
        Integer quantity,
        BigDecimal subtotal
) {
}
