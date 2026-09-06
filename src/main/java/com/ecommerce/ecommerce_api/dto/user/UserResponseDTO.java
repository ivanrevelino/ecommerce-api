package com.ecommerce.ecommerce_api.dto.user;

import com.ecommerce.ecommerce_api.models.enums.UserRoles;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String username,
        UserRoles role,
        LocalDateTime createdAt
) {
}
