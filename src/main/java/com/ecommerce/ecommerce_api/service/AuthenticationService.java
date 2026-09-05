package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.dto.login_register.LoginRequestDTO;
import com.ecommerce.ecommerce_api.dto.login_register.LoginResponseDTO;
import com.ecommerce.ecommerce_api.dto.login_register.RegisterRequestDTO;
import com.ecommerce.ecommerce_api.dto.login_register.RegisterResponseDTO;
import com.ecommerce.ecommerce_api.models.User;
import com.ecommerce.ecommerce_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authManager;
    private final UserRepository repository;
    private final BCryptPasswordEncoder bcrypt;
    private final JwtService jwtService;

    public ResponseEntity<LoginResponseDTO> login(LoginRequestDTO request) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
        );
        Authentication authentication = authManager.authenticate(authenticationToken);
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        log.info("User(id: {}, username: {}, role: {}) made login successfully", user.getId(), user.getUsername(), user.getRoles());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    public ResponseEntity<RegisterResponseDTO> register(RegisterRequestDTO request) {

        if (repository.findByUsername(request.username()) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String hashPassword = bcrypt.encode(request.password());

        User userToBeSaved = User.builder().username(request.username()).password(hashPassword).build();
        User saved = repository.save(userToBeSaved);

        RegisterResponseDTO registerResponseDTO = new RegisterResponseDTO(saved.getId(),
                saved.getUsername(),
                saved.getCreatedAt(),
                saved.getRoles()
        );

        log.info("User(id: {}, username: {}, role: {}) registered successfully", saved.getId(), saved.getUsername(), saved.getRoles());
        return new ResponseEntity<>(registerResponseDTO, HttpStatus.CREATED);
    }
}
