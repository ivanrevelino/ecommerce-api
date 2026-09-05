package com.ecommerce.ecommerce_api.dto.product;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        BigDecimal price,
        Long categoryId,
        String categoryName,
        Integer stock
) {
}
