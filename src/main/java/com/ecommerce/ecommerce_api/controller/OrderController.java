package com.ecommerce.ecommerce_api.controller;

import com.ecommerce.ecommerce_api.dto.order.OrderRequestDTO;
import com.ecommerce.ecommerce_api.dto.order.OrderResponseDTO;
import com.ecommerce.ecommerce_api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/orders")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Valid OrderRequestDTO request) {
        return new ResponseEntity<>(orderService.createOrder(request), HttpStatus.CREATED);
    }

    @GetMapping
    public Page<OrderResponseDTO> findAll(Pageable pageable) {
        return orderService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> returnOrder(@PathVariable Long orderId) {
        orderService.returnOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
