package com.ecommerce.ecommerce_api.repository;

import com.ecommerce.ecommerce_api.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);
}
