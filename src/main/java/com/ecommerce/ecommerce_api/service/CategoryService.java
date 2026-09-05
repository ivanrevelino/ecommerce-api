package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.dto.category.CategoryRequestDTO;
import com.ecommerce.ecommerce_api.dto.category.CategoryResponseDTO;
import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.models.Category;
import com.ecommerce.ecommerce_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public ResponseEntity<CategoryResponseDTO> create(CategoryRequestDTO request) {
        Category categoryToBeSaved = Category.builder()
                .name(request.name())
                .build();

        Category saved = categoryRepository.save(categoryToBeSaved);

        log.info("Category(id: {}, name: {}) created successfully", saved.getId(), saved.getName());
        return new ResponseEntity<>(toResponseDTO(saved), HttpStatus.CREATED);
    }

    public ResponseEntity<List<CategoryResponseDTO>> findAll() {
        List<CategoryResponseDTO> categories = categoryRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();

        return ResponseEntity.ok(categories);
    }

    public ResponseEntity<CategoryResponseDTO> findById(Long id) {
        Category category = findCategoryById(id);
        return ResponseEntity.ok(toResponseDTO(category));
    }

    public Category findByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    public ResponseEntity<CategoryResponseDTO> update(Long id, CategoryRequestDTO request) {
        Category category = findCategoryById(id);

        category.setName(request.name());

        Category updated = categoryRepository.save(category);

        log.info("Category(id: {}, name: {}) updated successfully", updated.getId(), updated.getName());
        return ResponseEntity.ok(toResponseDTO(updated));
    }

    public ResponseEntity<Void> delete(Long id) {
        Category category = findCategoryById(id);
        categoryRepository.delete(category);

        log.info("Category(id: {}, name: {}) deleted successfully", category.getId(), category.getName());
        return ResponseEntity.noContent().build();
    }

    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private CategoryResponseDTO toResponseDTO(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getName()
        );
    }
}
