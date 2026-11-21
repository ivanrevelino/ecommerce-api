package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.models.Category;
import com.ecommerce.ecommerce_api.repository.CategoryRepository;
import com.ecommerce.ecommerce_api.util.Generator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository repository;

    @InjectMocks
    private CategoryService service;

    @Test
    void createShouldReturnSavedCategory() {
        when(repository.save(any(Category.class))).thenAnswer(invocation -> {
            Category saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        assertThat(service.create(Generator.categoryRequest())).isEqualTo(Generator.categoryResponse());
    }

    @Test
    void findAllShouldMapCategories() {
        Category category = Generator.category();
        category.setId(1L);
        when(repository.findAll()).thenReturn(List.of(category));

        assertThat(service.findAll()).containsExactly(Generator.categoryResponse());
    }

    @Test
    void updateShouldChangeCategoryName() {
        Category category = Generator.category();
        category.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(repository.save(category)).thenReturn(category);

        assertThat(service.update(1L, Generator.categoryRequest())).isEqualTo(Generator.categoryResponse());
        verify(repository).save(category);
    }

    @Test
    void findByIdShouldThrowWhenCategoryDoesNotExist() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found with id: 404");
    }

    @Test
    void findByNameShouldReturnMatchingCategory() {
        Category category = Generator.category();
        category.setId(1L);
        when(repository.findByName("Food")).thenReturn(Optional.of(category));

        assertThat(service.findByName("Food")).isEqualTo(category);
    }

    @Test
    void deleteShouldRemoveExistingCategory() {
        Category category = Generator.category();
        when(repository.findById(1L)).thenReturn(Optional.of(category));

        service.delete(1L);

        verify(repository).delete(category);
    }
}
