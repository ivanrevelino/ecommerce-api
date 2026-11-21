package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.dto.login_register.LoginRequestDTO;
import com.ecommerce.ecommerce_api.dto.login_register.RegisterRequestDTO;
import com.ecommerce.ecommerce_api.exception.BadRequestException;
import com.ecommerce.ecommerce_api.models.User;
import com.ecommerce.ecommerce_api.models.enums.UserRoles;
import com.ecommerce.ecommerce_api.repository.UserRepository;
import com.ecommerce.ecommerce_api.security.jwt.JwtService;
import com.ecommerce.ecommerce_api.util.Generator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private UserRepository repository;

    @Mock
    private BCryptPasswordEncoder bcrypt;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService service;

    @Test
    void loginShouldReturnGeneratedToken() {
        User user = Generator.user();
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        when(authManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("signed-token");

        assertThat(service.login(Generator.loginRequest())).isEqualTo(Generator.loginResponse());
    }

    @Test
    void registerShouldSaveUserWithEncodedPasswordAndUserRole() {
        User savedUser = Generator.user();
        savedUser.setUsername("new-user");
        when(repository.findByUsername("new-user")).thenReturn(Optional.empty());
        when(bcrypt.encode("password")).thenReturn("encoded-password");
        when(repository.save(any(User.class))).thenReturn(savedUser);

        assertThat(service.register(Generator.registerRequest())).isEqualTo(Generator.registerResponse());
        assertThat(savedUser.getRoles()).isEqualTo(UserRoles.USER);
        verify(repository).save(any(User.class));
    }

    @Test
    void registerShouldRejectDuplicateUsername() {
        when(repository.findByUsername("new-user")).thenReturn(Optional.of(Generator.user()));

        assertThatThrownBy(() -> service.register(Generator.registerRequest()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Username already exists");
    }
}
