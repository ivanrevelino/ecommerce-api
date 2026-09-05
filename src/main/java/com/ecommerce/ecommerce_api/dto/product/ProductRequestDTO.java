package com.ecommerce.ecommerce_api.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank(message = "Product name cannot be null or empty")
        String name,

        @NotNull(message = "Product price cannot be null")
        @DecimalMin(value = "0.01", message = "Product price must be greater than zero")
        BigDecimal price,

        @NotNull(message = "Category id cannot be null")
        Long categoryId,

        @NotNull(message = "Product stock cannot be null")
        @Min(value = 0, message = "Product stock cannot be negative")
        Integer stock
) {
}
