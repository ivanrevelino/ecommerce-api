package com.ecommerce.ecommerce_api.controller;

import com.ecommerce.ecommerce_api.dto.LoginRequestDTO;
import com.ecommerce.ecommerce_api.dto.LoginResponseDTO;
import com.ecommerce.ecommerce_api.dto.RegisterRequestDTO;
import com.ecommerce.ecommerce_api.dto.RegisterResponseDTO;
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
