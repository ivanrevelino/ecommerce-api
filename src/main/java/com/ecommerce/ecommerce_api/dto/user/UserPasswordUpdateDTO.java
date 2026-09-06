package com.ecommerce.ecommerce_api.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserPasswordUpdateDTO(
        @NotBlank(message = "Current password cannot be null or empty")
        String currentPassword,

        @NotBlank(message = "New password cannot be null or empty")
        String newPassword
) {
}
