package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.dto.product.ProductRequestDTO;
import com.ecommerce.ecommerce_api.dto.product.ProductResponseDTO;
import com.ecommerce.ecommerce_api.exception.InsufficientStockException;
import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.models.Category;
import com.ecommerce.ecommerce_api.models.Product;
import com.ecommerce.ecommerce_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ResponseEntity<ProductResponseDTO> create(ProductRequestDTO request) {
        Category category = categoryService.findCategoryById(request.categoryId());

        Product productToBeSaved = Product.builder()
                .name(request.name())
                .price(request.price())
                .category(category)
                .stock(request.stock())
                .build();

        Product saved = productRepository.save(productToBeSaved);

        log.info("Product(id: {}, name: {}) created successfully", saved.getId(), saved.getName());
        return new ResponseEntity<>(toResponseDTO(saved), HttpStatus.CREATED);
    }

    public ResponseEntity<List<ProductResponseDTO>> findAll() {
        List<ProductResponseDTO> products = productRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();

        return ResponseEntity.ok(products);
    }

    public ResponseEntity<ProductResponseDTO> findById(Long id) {
        Product product = findProductById(id);
        return ResponseEntity.ok(toResponseDTO(product));
    }

    public ResponseEntity<ProductResponseDTO> update(Long id, ProductRequestDTO request) {
        Product product = findProductById(id);
        Category category = categoryService.findCategoryById(request.categoryId());

        product.setName(request.name());
        product.setPrice(request.price());
        product.setCategory(category);
        product.setStock(request.stock());

        Product updated = productRepository.save(product);

        log.info("Product(id: {}, name: {}) updated successfully", updated.getId(), updated.getName());
        return ResponseEntity.ok(toResponseDTO(updated));
    }

    public ResponseEntity<Void> delete(Long id) {
        Product product = findProductById(id);
        productRepository.delete(product);

        log.info("Product(id: {}, name: {}) deleted successfully", product.getId(), product.getName());
        return ResponseEntity.noContent().build();
    }

    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public void decreaseStock(Product product, Integer quantity) {
        if (product.getStock() < quantity) {
            throw new InsufficientStockException("Insufficient stock"); // sla oq escrever
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    private ProductResponseDTO toResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getStock()
        );
    }
}
