package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.dto.order.OrderRequestDTO;
import com.ecommerce.ecommerce_api.dto.order.OrderResponseDTO;
import com.ecommerce.ecommerce_api.dto.order_item.OrderItemRequestDTO;
import com.ecommerce.ecommerce_api.dto.order_item.OrderItemResponseDTO;
import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.mapper.OrderItemMapper;
import com.ecommerce.ecommerce_api.mapper.OrderMapper;
import com.ecommerce.ecommerce_api.models.Order;
import com.ecommerce.ecommerce_api.models.OrderItem;
import com.ecommerce.ecommerce_api.models.Product;
import com.ecommerce.ecommerce_api.models.User;
import com.ecommerce.ecommerce_api.models.enums.OrderStatus;
import com.ecommerce.ecommerce_api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;


    public Page<OrderResponseDTO> findAll(Pageable pageable) {
        User user = userService.getAuthenticatedUser();
        return repository.findByUser(user, pageable).map(orderMapper::toDto);
    }

    @Transactional
    public ResponseEntity<OrderResponseDTO> createOrder(OrderRequestDTO request) {
        User user = userService.getAuthenticatedUser();
        Order orderToBeSaved = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .build();
        BigDecimal total = BigDecimal.valueOf(0);
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequestDTO item : request.items()) {

            Product productById = productService.findProductById(item.productId());

            productService.decreaseStock(productById, item.quantity());
            total = total.add(calculateSubtotal(productById.getPrice(), item.quantity()));

            OrderItem orderItem = OrderItem.builder()
                    .price(productById.getPrice())
                    .product(productById)
                    .quantity(item.quantity())
                    .build();
            orderItem.setOrder(orderToBeSaved);
            orderItem.setSubtotal(calculateSubtotal(orderItem.getProduct().getPrice(), orderItem.getQuantity()));
            orderItems.add(orderItem);
        }

        orderToBeSaved.setOrderItems(orderItems);
        orderToBeSaved.setTotal(total);

        Order saved = repository.save(orderToBeSaved);
        List<OrderItemResponseDTO> itemResponseDTOS = orderItemMapper.toDto(saved.getOrderItems());

        OrderResponseDTO orderResponseDTO = orderMapper.toDto(saved);

        return new ResponseEntity<>(orderResponseDTO, HttpStatus.CREATED);
    }

    public ResponseEntity<OrderResponseDTO> findById(Long id) {
        User user = userService.getAuthenticatedUser();

        Order order = repository.findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        return new ResponseEntity<>(orderMapper.toDto(order), HttpStatus.ACCEPTED);
    }

    public Order findOrderById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id + " + id));
    }

    public BigDecimal calculateSubtotal(BigDecimal productPrice, Integer quantity) {
        return productPrice.multiply(BigDecimal.valueOf(quantity));
    }
}