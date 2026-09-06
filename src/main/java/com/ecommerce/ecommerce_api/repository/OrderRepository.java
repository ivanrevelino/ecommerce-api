package com.ecommerce.ecommerce_api.repository;

import com.ecommerce.ecommerce_api.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
