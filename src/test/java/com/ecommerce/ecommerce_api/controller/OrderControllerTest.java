package com.ecommerce.ecommerce_api.controller;

import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.infra.handler.RestExceptionHandler;
import com.ecommerce.ecommerce_api.security.filters.SecurityFilter;
import com.ecommerce.ecommerce_api.security.jwt.JwtService;
import com.ecommerce.ecommerce_api.service.OrderService;
import com.ecommerce.ecommerce_api.util.Generator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(RestExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SecurityFilter securityFilter;

    @Test
    void createOrderShouldReturnCreatedOrder() throws Exception {
        when(orderService.createOrder(any())).thenReturn(Generator.orderResponse());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"items":[{"productId":1,"quantity":2}]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.total").value(20));
    }

    @Test
    void findAllShouldReturnOrdersPage() throws Exception {
        when(orderService.findAll(any())).thenReturn(new PageImpl<>(
                List.of(Generator.orderResponse()),
                PageRequest.of(0, 10),
                1
        ));

        mockMvc.perform(get("/orders").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void findByIdShouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        when(orderService.findById(404L)).thenThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(get("/orders/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found"));
    }

    @Test
    void findByIdShouldReturnOrder() throws Exception {
        when(orderService.findById(1L)).thenReturn(Generator.orderResponse());

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test-user"));
    }

    @Test
    void createOrderShouldRejectEmptyItems() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"items":[]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void cancelOrderShouldReturnNoContent() throws Exception {
        doNothing().when(orderService).cancelOrder(1L);

        mockMvc.perform(patch("/orders/1/cancel"))
                .andExpect(status().isNoContent());
    }
}
