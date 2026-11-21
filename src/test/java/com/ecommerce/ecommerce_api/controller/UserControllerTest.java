package com.ecommerce.ecommerce_api.controller;

import com.ecommerce.ecommerce_api.dto.user.UserUpdateDTO;
import com.ecommerce.ecommerce_api.exception.BadRequestException;
import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.infra.handler.RestExceptionHandler;
import com.ecommerce.ecommerce_api.security.filters.SecurityFilter;
import com.ecommerce.ecommerce_api.security.jwt.JwtService;
import com.ecommerce.ecommerce_api.service.UserService;
import com.ecommerce.ecommerce_api.util.Generator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SecurityFilter securityFilter;

    @Test
    void findByUsernameShouldReturnMatchingUser() throws Exception {
        when(userService.findByUsername("test-user")).thenReturn(Generator.userResponse());

        mockMvc.perform(get("/users/filter/test-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test-user"));
    }

    @Test
    void findAllShouldReturnUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(Generator.userResponse()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("test-user"));
    }

    @Test
    void findByIdShouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        when(userService.findById(404L)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/users/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void updateShouldReturnUpdatedUser() throws Exception {
        when(userService.update(eq(1L), any(UserUpdateDTO.class)))
                .thenReturn(Generator.userResponse());

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"updated-user","role":"ADMIN"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateShouldReturnBadRequestWhenUsernameIsBlank() throws Exception {
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":" ","role":"USER"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateShouldReturnBadRequestWhenServiceRejectsDuplicateUsername() throws Exception {
        when(userService.update(eq(1L), any(UserUpdateDTO.class)))
                .thenThrow(new BadRequestException("Username already exists"));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"updated-user","role":"ADMIN"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void updatePasswordShouldReturnNoContent() throws Exception {
        doNothing().when(userService).updatePassword(eq(1L), any());

        mockMvc.perform(patch("/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"currentPassword":"old-password","newPassword":"new-password"}
                                """))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}
