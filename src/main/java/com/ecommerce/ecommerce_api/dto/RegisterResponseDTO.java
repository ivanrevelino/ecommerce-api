package com.ecommerce.ecommerce_api.dto;

import com.ecommerce.ecommerce_api.models.enums.UserRoles;

import java.time.LocalDateTime;

public record RegisterResponseDTO (Long id, String username, LocalDateTime createdAt, UserRoles role) {}
