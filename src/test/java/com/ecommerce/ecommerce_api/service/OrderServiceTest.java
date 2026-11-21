package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.dto.order.OrderRequestDTO;
import com.ecommerce.ecommerce_api.dto.order.OrderResponseDTO;
import com.ecommerce.ecommerce_api.dto.order_item.OrderItemRequestDTO;
import com.ecommerce.ecommerce_api.dto.order_item.OrderItemResponseDTO;
import com.ecommerce.ecommerce_api.exception.InsufficientStockException;
import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.mapper.OrderMapper;
import com.ecommerce.ecommerce_api.models.*;
import com.ecommerce.ecommerce_api.models.enums.OrderStatus;
import com.ecommerce.ecommerce_api.models.enums.UserRoles;
import com.ecommerce.ecommerce_api.repository.OrderRepository;
import com.ecommerce.ecommerce_api.util.Generator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Should create order when successful")
    void createOrder() {
        User user = new User(1L, "srmbilane", "123456", LocalDateTime.now(), UserRoles.ADMIN);

        Product product1 = new Product(1L, "banana", new BigDecimal(BigInteger.valueOf(30000)), new Category(1L, "Fruits"), 4);
        Product product3 = new Product(3L, "uva", new BigDecimal(BigInteger.valueOf(5000)), new Category(1L, "Fruits"), 3);

        when(userService.getAuthenticatedUser())
                .thenReturn(user);

        when(productService.findProductById(product1.getId())).thenReturn(product1);
        when(productService.findProductById(product3.getId())).thenReturn(product3);

        OrderRequestDTO request = new OrderRequestDTO(List.of(
                new OrderItemRequestDTO(product1.getId(), 2),
                new OrderItemRequestDTO(product3.getId(), 3)
        ));

        OrderItemResponseDTO orderItemResponseDTO1 = new OrderItemResponseDTO(
                1L,
                product1.getId(),
                product1.getName(),
                product1.getPrice(),
                2,
                BigDecimal.valueOf(60000)
        );

        OrderItemResponseDTO orderItemResponseDTO2 = new OrderItemResponseDTO(
                2L,
                product3.getId(),
                product3.getName(),
                product3.getPrice(),
                3,
                BigDecimal.valueOf(15000)
        );

        OrderResponseDTO expectedResponse = new OrderResponseDTO(
                1L,
                user.getId(),
                user.getUsername(),
                List.of(orderItemResponseDTO1, orderItemResponseDTO2),
                BigDecimal.valueOf(75000),
                OrderStatus.PENDING,
                LocalDateTime.now()
        );

        when(repository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            order.setStatus(OrderStatus.PENDING);
            order.setUser(user);
            return order;
        });

        when(orderMapper.toDto(any(Order.class))).thenReturn(expectedResponse);

        OrderResponseDTO order = orderService.createOrder(request);

        Assertions.assertThat(order.total()).isEqualByComparingTo(BigDecimal.valueOf(75000));
        Assertions.assertThat(order.userId()).isEqualTo(user.getId());
        Assertions.assertThat(order.username()).isEqualTo(user.getUsername());

    }

    @Test
    @DisplayName("Should throw InsufficientStockException when stock is not sufficient")
    void createOrderShouldThrowInsufficientStockWhenStockIsNotSufficient() {
        Product product1 = new Product(1L, "banana", new BigDecimal(BigInteger.valueOf(30000)), new Category(1L, "Fruits"), 4);

        when(userService.getAuthenticatedUser())
                .thenReturn(new User(1L, "srmbilane", "123456", LocalDateTime.now(), UserRoles.ADMIN));

        when(productService.findProductById(product1.getId())).thenReturn(product1);

        doThrow(InsufficientStockException.class).when(productService).decreaseStock(product1, 5);

        OrderRequestDTO request = new OrderRequestDTO(List.of(
                new OrderItemRequestDTO(product1.getId(), 5)
        ));

        Assertions.assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(InsufficientStockException.class);

    }

    @Test
    void findAllShouldReturnAuthenticatedUsersOrders() {
        User user = Generator.user();
        Order order = Order.builder().id(1L).user(user).build();
        OrderResponseDTO response = Generator.orderResponse();
        var pageable = PageRequest.of(0, 10);
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(repository.findByUser(user, pageable)).thenReturn(new PageImpl<>(List.of(order)));
        when(orderMapper.toDto(order)).thenReturn(response);

        Assertions.assertThat(orderService.findAll(pageable).getContent()).containsExactly(response);
    }

    @Test
    void findByIdShouldReturnOrderForAuthenticatedUser() {
        User user = Generator.user();
        Order order = Order.builder().id(1L).user(user).build();
        OrderResponseDTO response = Generator.orderResponse();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(repository.findByIdAndUser(1L, user)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(response);

        Assertions.assertThat(orderService.findById(1L)).isEqualTo(response);
    }

    @Test
    void findByIdShouldThrowWhenOrderDoesNotExistForUser() {
        User user = Generator.user();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(repository.findByIdAndUser(404L, user)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> orderService.findById(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order not found");
    }

    @Test
    void cancelOrderShouldRestockProductsAndMarkOrderCancelled() {
        User user = Generator.user();
        Product product = Generator.product();
        product.setStock(5);
        OrderItem item = Generator.orderItem();
        item.setProduct(product);
        item.setOrder(null);
        Order order = Order.builder()
                .id(1L)
                .user(user)
                .status(OrderStatus.PENDING)
                .orderItems(List.of(item))
                .build();
        item.setOrder(order);
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(repository.findByIdAndUser(1L, user)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        Assertions.assertThat(product.getStock()).isEqualTo(7);
        Assertions.assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(productService).save(product);
        verify(repository).save(order);
    }

    @Test
    void cancelOrderShouldThrowWhenOrderDoesNotExistForUser() {
        User user = Generator.user();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(repository.findByIdAndUser(404L, user)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> orderService.cancelOrder(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order not found");
    }

    @Test
    void cancelOrderShouldRejectNonPendingOrder() {
        User user = Generator.user();
        Order order = Order.builder().id(1L).user(user).status(OrderStatus.CANCELLED).build();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(repository.findByIdAndUser(1L, user)).thenReturn(Optional.of(order));

        Assertions.assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("You cannot cancel this order");
    }

    @Test
    void calculateSubtotalShouldMultiplyPriceByQuantity() {
        Assertions.assertThat(orderService.calculateSubtotal(BigDecimal.TEN, 3))
                .isEqualByComparingTo(BigDecimal.valueOf(30));
    }

}