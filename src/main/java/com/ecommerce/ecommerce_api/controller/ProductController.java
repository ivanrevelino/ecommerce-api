package com.ecommerce.ecommerce_api.controller;

import com.ecommerce.ecommerce_api.dto.product.ProductRequestDTO;
import com.ecommerce.ecommerce_api.dto.product.ProductResponseDTO;
import com.ecommerce.ecommerce_api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/products")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Page<ProductResponseDTO> findAll(Pageable pageable) {
        return productService.findAll(pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> create(@RequestBody @Valid ProductRequestDTO request) {
        return productService.create(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id,
                                                     @RequestBody @Valid ProductRequestDTO request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return productService.delete(id);
    }
}
