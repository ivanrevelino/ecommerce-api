package com.ecommerce.ecommerce_api.repository;

import com.ecommerce.ecommerce_api.models.Order;
import com.ecommerce.ecommerce_api.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUser(User user, Pageable pageable);
    Optional<Order> findByIdAndUser(Long id, User user);

}
