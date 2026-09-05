package com.ecommerce.ecommerce_api.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDTO(
        @NotBlank(message = "Category name cannot be null or empty")
        String name
) {
}
