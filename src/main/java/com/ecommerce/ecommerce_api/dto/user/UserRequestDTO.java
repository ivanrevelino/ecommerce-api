package com.ecommerce.ecommerce_api.dto.user;

import com.ecommerce.ecommerce_api.models.enums.UserRoles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestDTO(
        @NotBlank(message = "Username cannot be null or empty")
        String username,

        @NotBlank(message = "Password cannot be null or empty")
        String password,

        @NotNull(message = "User role cannot be null")
        UserRoles role
) {
}
