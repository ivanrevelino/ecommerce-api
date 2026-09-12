package com.ecommerce.ecommerce_api.repository;

import com.ecommerce.ecommerce_api.models.Category;
import com.ecommerce.ecommerce_api.models.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Should create Product when Successful")
    void shouldCreateProductWhenSuccessful() {
        Product saved = generate();
        Assertions.assertThat(saved.getId()).isNotNull();
    }

    @Test
    @DisplayName("Should delete product when Successful")
    void shouldDeleteProductWhenSuccessful() {

        Product saved = generate();

        Assertions.assertThat(saved.getId()).isNotNull();

        productRepository.delete(saved);
        Optional<Product> productById = productRepository.findById(saved.getId());

        Assertions.assertThat(productById).isEmpty();
    }

    @Test
    @DisplayName("FindById should return right project when Successful")
    void findByIdShouldReturnRightProjectWhenSuccessful() {
        Product product = generate();

        Optional<Product> productById = productRepository.findById(product.getId());

        Assertions.assertThat(productById).isPresent();
        Assertions.assertThat(productById.get().getId()).isEqualTo(product.getId());
        Assertions.assertThat(productById.get().getName()).isEqualTo("Banana");
    }

    public Product generate() {
        Category category = categoryRepository.save(Category.builder().name("Alimentos").build());

        Product product = Product.builder()
                .price(BigDecimal.valueOf(2000))
                .name("Banana")
                .stock(10)
                .build();

        product.setCategory(category);
        productRepository.save(product);

        return product;
    }
}