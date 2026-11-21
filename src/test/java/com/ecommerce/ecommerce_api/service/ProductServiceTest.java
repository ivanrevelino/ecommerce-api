package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.dto.product.ProductRequestDTO;
import com.ecommerce.ecommerce_api.exception.InsufficientStockException;
import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.models.Category;
import com.ecommerce.ecommerce_api.models.Product;
import com.ecommerce.ecommerce_api.repository.ProductRepository;
import com.ecommerce.ecommerce_api.util.Generator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductService service;

    @Test
    void createShouldReturnSavedProduct() {
        Category category = Generator.category();
        category.setId(1L);
        when(categoryService.findCategoryById(1L)).thenReturn(category);
        when(repository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        assertThat(service.create(Generator.productRequest())).isEqualTo(Generator.productResponse());
    }

    @Test
    void updateShouldReplaceProductFields() {
        Category category = Generator.category();
        category.setId(1L);
        Product product = Generator.product();
        product.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryService.findCategoryById(1L)).thenReturn(category);
        when(repository.save(product)).thenReturn(product);

        ProductRequestDTO request = new ProductRequestDTO("Banana", product.getPrice(), 1L, 10);
        assertThat(service.update(1L, request)).isEqualTo(Generator.productResponse());
        verify(repository).save(product);
    }

    @Test
    void findAllShouldMapProducts() {
        Category category = Generator.category();
        category.setId(1L);
        Product product = Generator.product();
        product.setId(1L);
        product.setCategory(category);
        var pageable = PageRequest.of(0, 10);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(product)));

        assertThat(service.findAll(pageable).getContent()).containsExactly(Generator.productResponse());
    }

    @Test
    void deleteShouldRemoveExistingProduct() {
        Product product = Generator.product();
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.delete(1L);

        verify(repository).delete(product);
    }

    @Test
    void decreaseStockShouldPersistNewStock() {
        Product product = Generator.product();
        service.decreaseStock(product, 3);

        assertThat(product.getStock()).isEqualTo(7);
        verify(repository).save(product);
    }

    @Test
    void decreaseStockShouldThrowWhenStockIsInsufficient() {
        Product product = Generator.product();

        assertThatThrownBy(() -> service.decreaseStock(product, 11))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void findProductByIdShouldThrowWhenProductDoesNotExist() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findProductById(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found with id: 404");
    }
}
