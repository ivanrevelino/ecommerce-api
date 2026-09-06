package com.ecommerce.ecommerce_api.mapper;

import com.ecommerce.ecommerce_api.dto.order.OrderResponseDTO;
import com.ecommerce.ecommerce_api.dto.order_item.OrderItemResponseDTO;
import com.ecommerce.ecommerce_api.models.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderResponseDTO toDto(Order order) {

        List<OrderItemResponseDTO> itemResponseDTOS = orderItemMapper.toDto(order.getOrderItems());

        return new OrderResponseDTO(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getUsername(),
                itemResponseDTOS, order.getTotal(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
