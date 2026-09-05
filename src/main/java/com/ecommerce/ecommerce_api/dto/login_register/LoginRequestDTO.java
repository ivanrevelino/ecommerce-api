package com.ecommerce.ecommerce_api.dto.login_register;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO (@NotBlank(message = "Username cannot be null or empty") String username,
                               @NotBlank(message = "Username cannot be null or empty") String password){}
