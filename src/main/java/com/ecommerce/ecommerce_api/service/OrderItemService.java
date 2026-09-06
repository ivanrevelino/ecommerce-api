package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.dto.order_item.OrderItemResponseDTO;
import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.models.OrderItem;
import com.ecommerce.ecommerce_api.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository repository;

    public List<OrderItem> saveAll(List<OrderItem> orderItem) {
        return repository.saveAll(orderItem);
    }

    public OrderItem findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));
    }

    public OrderItemResponseDTO toDto(OrderItem orderItems) {
        return new OrderItemResponseDTO(orderItems.getId(),
                orderItems.getProduct().getId(),
                orderItems.getProduct().getName(),
                orderItems.getPrice(),
                orderItems.getQuantity(),
                orderItems.getPrice().multiply(BigDecimal.valueOf(orderItems.getQuantity())));
    }


}
