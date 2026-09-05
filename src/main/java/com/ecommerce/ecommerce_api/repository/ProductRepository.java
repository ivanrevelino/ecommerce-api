package com.ecommerce.ecommerce_api.repository;

import com.ecommerce.ecommerce_api.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
