package com.ecommerce.ecommerce_api.mapper;

import com.ecommerce.ecommerce_api.dto.order_item.OrderItemResponseDTO;
import com.ecommerce.ecommerce_api.models.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderItemMapper {

    public OrderItemResponseDTO toDto(OrderItem orderItems) {
        return new OrderItemResponseDTO(orderItems.getId(),
                orderItems.getProduct().getId(),
                orderItems.getProduct().getName(),
                orderItems.getPrice(),
                orderItems.getQuantity(),
                orderItems.getSubtotal()
        );
    }

    public List<OrderItemResponseDTO> toDto(List<OrderItem> orderItemResponseDTOList) {
        return orderItemResponseDTOList.stream().map(this::toDto).toList();
    }
}
