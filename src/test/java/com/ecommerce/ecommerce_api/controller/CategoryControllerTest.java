package com.ecommerce.ecommerce_api.controller;

import com.ecommerce.ecommerce_api.dto.category.CategoryRequestDTO;
import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.infra.handler.RestExceptionHandler;
import com.ecommerce.ecommerce_api.security.filters.SecurityFilter;
import com.ecommerce.ecommerce_api.security.jwt.JwtService;
import com.ecommerce.ecommerce_api.service.CategoryService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SecurityFilter securityFilter;

    @Test
    void createShouldReturnCreatedCategory() throws Exception {
        when(categoryService.create(any(CategoryRequestDTO.class))).thenReturn(Generator.categoryResponse());

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Food"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Food"));
    }

    @Test
    void findByIdShouldReturnCategory() throws Exception {
        when(categoryService.findById(1L)).thenReturn(Generator.categoryResponse());

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Food"));
    }

    @Test
    void findAllShouldReturnCategories() throws Exception {
        when(categoryService.findAll()).thenReturn(List.of(Generator.categoryResponse()));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Food"));
    }

    @Test
    void updateShouldReturnUpdatedCategory() throws Exception {
        when(categoryService.update(org.mockito.ArgumentMatchers.eq(1L), any(CategoryRequestDTO.class)))
                .thenReturn(Generator.categoryResponse());

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Food"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        doNothing().when(categoryService).delete(1L);

        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenCategoryDoesNotExist() throws Exception {
        when(categoryService.findById(404L)).thenThrow(new ResourceNotFoundException("Category not found"));

        mockMvc.perform(get("/categories/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found"));
    }

    @Test
    void createShouldReturnBadRequestWhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":" "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
