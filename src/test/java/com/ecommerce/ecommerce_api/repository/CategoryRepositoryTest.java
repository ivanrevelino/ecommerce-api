package com.ecommerce.ecommerce_api.repository;

import com.ecommerce.ecommerce_api.models.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repository;

    @Test
    void findByNameShouldReturnMatchingCategory() {
        Category saved = repository.save(Category.builder().name("Food").build());

        assertThat(repository.findByName("Food")).contains(saved);
    }

    @Test
    void findByNameShouldReturnEmptyWhenCategoryDoesNotExist() {
        assertThat(repository.findByName("missing")).isEmpty();
    }
}
