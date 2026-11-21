package com.ecommerce.ecommerce_api.controller;

import com.ecommerce.ecommerce_api.dto.product.ProductRequestDTO;
import com.ecommerce.ecommerce_api.exception.BadRequestException;
import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.infra.handler.RestExceptionHandler;
import com.ecommerce.ecommerce_api.security.filters.SecurityFilter;
import com.ecommerce.ecommerce_api.security.jwt.JwtService;
import com.ecommerce.ecommerce_api.service.ProductService;
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

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SecurityFilter securityFilter;

    @Test
    void createShouldReturnCreatedProduct() throws Exception {
        when(productService.create(any(ProductRequestDTO.class))).thenReturn(Generator.productResponse());

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Banana","price":10,"categoryId":1,"stock":10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Banana"))
                .andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    void findByIdShouldReturnProduct() throws Exception {
        when(productService.findById(1L)).thenReturn(Generator.productResponse());

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.price").value(10));
    }

    @Test
    void findAllShouldReturnProductsPage() throws Exception {
        when(productService.findAll(any())).thenReturn(new org.springframework.data.domain.PageImpl<>(
                List.of(Generator.productResponse())
        ));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Banana"));
    }

    @Test
    void updateShouldReturnUpdatedProduct() throws Exception {
        when(productService.update(org.mockito.ArgumentMatchers.eq(1L), any(ProductRequestDTO.class)))
                .thenReturn(Generator.productResponse());

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Banana","price":10,"categoryId":1,"stock":10}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        doNothing().when(productService).delete(1L);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        when(productService.findById(404L))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 404"));

        mockMvc.perform(get("/products/404"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","price":0,"categoryId":null,"stock":-1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createShouldReturnBadRequestWhenServiceRejectsRequest() throws Exception {
        when(productService.create(any(ProductRequestDTO.class)))
                .thenThrow(new BadRequestException("Invalid product"));

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Banana","price":10,"categoryId":1,"stock":10}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid product"));
    }
}
