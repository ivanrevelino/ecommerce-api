package com.ecommerce.ecommerce_api.controller;

import com.ecommerce.ecommerce_api.dto.login_register.LoginRequestDTO;
import com.ecommerce.ecommerce_api.dto.login_register.RegisterRequestDTO;
import com.ecommerce.ecommerce_api.exception.BadRequestException;
import com.ecommerce.ecommerce_api.infra.handler.RestExceptionHandler;
import com.ecommerce.ecommerce_api.security.filters.SecurityFilter;
import com.ecommerce.ecommerce_api.security.jwt.JwtService;
import com.ecommerce.ecommerce_api.service.AuthenticationService;
import com.ecommerce.ecommerce_api.util.Generator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SecurityFilter securityFilter;

    @Test
    void loginShouldReturnTokenWhenRequestIsValid() throws Exception {
        when(authenticationService.login(any(LoginRequestDTO.class))).thenReturn(Generator.loginResponse());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"test-user","password":"password"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("signed-token"));
    }

    @Test
    void registerShouldReturnCreatedWhenRequestIsValid() throws Exception {
        when(authenticationService.register(any(RegisterRequestDTO.class))).thenReturn(Generator.registerResponse());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"new-user","password":"password"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("new-user"));
    }

    @Test
    void registerShouldReturnBadRequestWhenUsernameAlreadyExists() throws Exception {
        when(authenticationService.register(any(RegisterRequestDTO.class)))
                .thenThrow(new BadRequestException("Username already exists"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"new-user","password":"password"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void loginShouldReturnBadRequestWhenRequiredFieldsAreBlank() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"","password":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
