package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.dto.login_register.LoginRequestDTO;
import com.ecommerce.ecommerce_api.dto.login_register.LoginResponseDTO;
import com.ecommerce.ecommerce_api.dto.login_register.RegisterRequestDTO;
import com.ecommerce.ecommerce_api.dto.login_register.RegisterResponseDTO;
import com.ecommerce.ecommerce_api.exception.BadRequestException;
import com.ecommerce.ecommerce_api.models.User;
import com.ecommerce.ecommerce_api.models.enums.UserRoles;
import com.ecommerce.ecommerce_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public LoginResponseDTO login(LoginRequestDTO request) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
        );
        Authentication authentication = authManager.authenticate(authenticationToken);
        User user = (User) authentication.getPrincipal();
        assert user != null;
        String token = jwtService.generateToken(user);

        log.info("User(id: {}, username: {}, role: {}) made login successfully", user.getId(), user.getUsername(), user.getRoles());
        return new LoginResponseDTO(token);
    }

    public RegisterResponseDTO register(RegisterRequestDTO request) {

        if (repository.findByUsername(request.username()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        String hashPassword = bcrypt.encode(request.password());

        User userToBeSaved = User.builder().username(request.username()).password(hashPassword).roles(UserRoles.USER).build();
        User saved = repository.save(userToBeSaved);

        RegisterResponseDTO registerResponseDTO = new RegisterResponseDTO(saved.getId(),
                saved.getUsername(),
                saved.getCreatedAt(),
                saved.getRoles()
        );

        log.info("User(id: {}, username: {}, role: {}) registered successfully", saved.getId(), saved.getUsername(), saved.getRoles());
        return registerResponseDTO;
    }
}
