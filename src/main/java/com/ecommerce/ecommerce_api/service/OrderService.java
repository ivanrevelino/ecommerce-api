package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.dto.order.OrderRequestDTO;
import com.ecommerce.ecommerce_api.dto.order.OrderResponseDTO;
import com.ecommerce.ecommerce_api.dto.order_item.OrderItemRequestDTO;
import com.ecommerce.ecommerce_api.dto.order_item.OrderItemResponseDTO;
import com.ecommerce.ecommerce_api.models.Order;
import com.ecommerce.ecommerce_api.models.OrderItem;
import com.ecommerce.ecommerce_api.models.Product;
import com.ecommerce.ecommerce_api.models.User;
import com.ecommerce.ecommerce_api.models.enums.OrderStatus;
import com.ecommerce.ecommerce_api.repository.OrderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final UserService userService;
    private final ProductService productService;

    @Transactional
    public ResponseEntity<OrderResponseDTO> createOrder(OrderRequestDTO request) {
        User user = userService.getAuthenticatedUser();
        Order orderToBeSaved = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .build();
        BigDecimal total = BigDecimal.valueOf(0);
        List<OrderItem> orderItems = new ArrayList<>();

        for (@Valid OrderItemRequestDTO item : request.items()) {

            Product productById = productService.findProductById(item.productId());

            productService.decreaseStock(productById, item.quantity());
            total = total.add(calculateSubtotal(productById.getPrice(), item.quantity()));

            OrderItem orderItem = OrderItem.builder()
                    .price(productById.getPrice())
                    .product(productById)
                    .quantity(item.quantity())
                    .build();
            orderItem.setOrder(orderToBeSaved);
            orderItems.add(orderItem);
        }

        orderToBeSaved.setOrderItems(orderItems);
        orderToBeSaved.setTotal(total);

        Order saved = repository.save(orderToBeSaved);
        List<OrderItemResponseDTO> itemResponseDTOS = toDto(saved.getOrderItems());

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(
                saved.getId(),
                saved.getUser().getId(),
                saved.getUser().getUsername(),
                itemResponseDTOS, saved.getTotal(),
                saved.getStatus(),
                saved.getCreatedAt()
        );

        return new ResponseEntity<>(orderResponseDTO, HttpStatus.CREATED);
    }

    public BigDecimal calculateSubtotal(BigDecimal productPrice, Integer quantity) {
        return productPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public OrderItemResponseDTO toDto(OrderItem orderItems) {
        return new OrderItemResponseDTO(orderItems.getId(),
                orderItems.getProduct().getId(),
                orderItems.getProduct().getName(),
                orderItems.getPrice(),
                orderItems.getQuantity(),
                calculateSubtotal(orderItems.getPrice(), orderItems.getQuantity()));
    }

    public List<OrderItemResponseDTO> toDto(List<OrderItem> orderItemResponseDTOList) {
        return orderItemResponseDTOList.stream().map(this::toDto).toList();
    }


}
