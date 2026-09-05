package com.ecommerce.ecommerce_api.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO (@NotBlank(message = "Username cannot be null or empty") String username,
                                  @NotBlank(message = "Password cannot be null or empty") String password){}
