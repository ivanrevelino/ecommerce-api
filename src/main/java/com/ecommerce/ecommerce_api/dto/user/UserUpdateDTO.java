package com.ecommerce.ecommerce_api.dto.user;

import com.ecommerce.ecommerce_api.models.enums.UserRoles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateDTO(
        @NotBlank(message = "Username cannot be null or empty")
        String username,

        @NotNull(message = "User role cannot be null")
        UserRoles role
) {
}
