package com.ecommerce.ecommerce_api.controller;

import com.ecommerce.ecommerce_api.dto.login_register.LoginRequestDTO;
import com.ecommerce.ecommerce_api.dto.login_register.LoginResponseDTO;
import com.ecommerce.ecommerce_api.dto.login_register.RegisterRequestDTO;
import com.ecommerce.ecommerce_api.dto.login_register.RegisterResponseDTO;
import com.ecommerce.ecommerce_api.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for authentication and user registration")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "User login",
            description = "Authenticates a user when successful and returns an JWT access token")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user",
            description = "Creates a new user account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "404", description = "Username already exists")
    })
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody @Valid RegisterRequestDTO request) {
        return new ResponseEntity<>(authenticationService.register(request), HttpStatus.CREATED);
    }
}
