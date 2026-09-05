package com.ecommerce.ecommerce_api.controller;

import com.ecommerce.ecommerce_api.dto.login_register.LoginRequestDTO;
import com.ecommerce.ecommerce_api.dto.login_register.LoginResponseDTO;
import com.ecommerce.ecommerce_api.dto.login_register.RegisterRequestDTO;
import com.ecommerce.ecommerce_api.dto.login_register.RegisterResponseDTO;
import com.ecommerce.ecommerce_api.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        return authenticationService.login(request);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody @Valid RegisterRequestDTO request) {
        return authenticationService.register(request);
    }
}
