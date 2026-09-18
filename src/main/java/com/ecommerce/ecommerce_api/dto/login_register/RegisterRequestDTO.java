package com.ecommerce.ecommerce_api.dto.login_register;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
        @NotBlank(message = "Username cannot be null or empty")
        @Schema(example = "Vladimir")
        String username,

        @NotBlank(message = "Password cannot be null or empty")
        @Schema(example = "Banana123") String password
) {
}
