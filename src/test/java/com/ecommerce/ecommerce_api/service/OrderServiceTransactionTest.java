package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.dto.order.OrderRequestDTO;
import com.ecommerce.ecommerce_api.dto.order_item.OrderItemRequestDTO;
import com.ecommerce.ecommerce_api.exception.InsufficientStockException;
import com.ecommerce.ecommerce_api.models.Category;
import com.ecommerce.ecommerce_api.models.Order;
import com.ecommerce.ecommerce_api.models.OrderItem;
import com.ecommerce.ecommerce_api.models.Product;
import com.ecommerce.ecommerce_api.models.User;
import com.ecommerce.ecommerce_api.models.enums.OrderStatus;
import com.ecommerce.ecommerce_api.models.enums.UserRoles;
import com.ecommerce.ecommerce_api.repository.CategoryRepository;
import com.ecommerce.ecommerce_api.repository.OrderRepository;
import com.ecommerce.ecommerce_api.repository.ProductRepository;
import com.ecommerce.ecommerce_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:order-service-tests;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OrderServiceTransactionTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    @MockitoSpyBean
    private ProductService productService;

    @Test
    void concurrentOrdersDoNotOversellStock() throws Exception {
        User user = createUser();
        Product product = createProduct("Banana", 5);
        when(userService.getAuthenticatedUser()).thenReturn(user);
        long ordersBefore = orderRepository.count();

        CountDownLatch bothRequestsReadStock = new CountDownLatch(2);
        doAnswer(invocation -> {
            Product loadedProduct = (Product) invocation.callRealMethod();
            if (loadedProduct.getId().equals(product.getId())) {
                bothRequestsReadStock.countDown();
                if (!bothRequestsReadStock.await(10, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Concurrent requests did not reach the stock check");
                }
            }
            return loadedProduct;
        }).when(productService).findProductById(product.getId());

        OrderRequestDTO request = request(product, 4);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<RuntimeException> first = executor.submit(() -> placeOrder(request));
            Future<RuntimeException> second = executor.submit(() -> placeOrder(request));

            RuntimeException firstFailure = first.get(15, TimeUnit.SECONDS);
            RuntimeException secondFailure = second.get(15, TimeUnit.SECONDS);

            assertThat(java.util.stream.Stream.of(firstFailure, secondFailure)
                    .filter(failure -> failure == null).count())
                    .isEqualTo(1);
            RuntimeException failure = firstFailure != null ? firstFailure : secondFailure;
            assertThat(hasCause(failure, OptimisticLockingFailureException.class)).isTrue();
            assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(1);
            assertThat(orderRepository.count()).isEqualTo(ordersBefore + 1);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void createOrderRollsBackEarlierStockChangesWhenLaterItemHasInsufficientStock() {
        User user = createUser();
        Product available = createProduct("Banana", 5);
        Product insufficient = createProduct("Grape", 1);
        when(userService.getAuthenticatedUser()).thenReturn(user);
        long ordersBefore = orderRepository.count();

        assertThatThrownBy(() -> orderService.createOrder(new OrderRequestDTO(List.of(
                new OrderItemRequestDTO(available.getId(), 2),
                new OrderItemRequestDTO(insufficient.getId(), 2)
        )))).isInstanceOf(InsufficientStockException.class);

        assertThat(productRepository.findById(available.getId()).orElseThrow().getStock()).isEqualTo(5);
        assertThat(productRepository.findById(insufficient.getId()).orElseThrow().getStock()).isEqualTo(1);
        assertThat(orderRepository.count()).isEqualTo(ordersBefore);
    }

    @Test
    void cancelOrderRollsBackStockAndStatusWhenRestockFails() {
        User user = createUser();
        Product firstProduct = createProduct("Banana", 5);
        Product secondProduct = createProduct("Grape", 7);
        OrderItem firstItem = orderItem(firstProduct, 2);
        OrderItem secondItem = orderItem(secondProduct, 3);
        Order orderToSave = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .total(BigDecimal.valueOf(50))
                .orderItems(List.of(firstItem, secondItem))
                .build();
        firstItem.setOrder(orderToSave);
        secondItem.setOrder(orderToSave);
        Order order = orderRepository.save(orderToSave);
        when(userService.getAuthenticatedUser()).thenReturn(user);

        AtomicInteger saveCalls = new AtomicInteger();
        doAnswer(invocation -> {
            if (saveCalls.incrementAndGet() == 2) {
                throw new IllegalStateException("Simulated product persistence failure");
            }
            return invocation.callRealMethod();
        }).when(productService).save(any(Product.class));

        assertThatThrownBy(() -> orderService.cancelOrder(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Simulated product persistence failure");

        assertThat(productRepository.findById(firstProduct.getId()).orElseThrow().getStock()).isEqualTo(5);
        assertThat(productRepository.findById(secondProduct.getId()).orElseThrow().getStock()).isEqualTo(7);
        assertThat(orderRepository.findById(order.getId()).orElseThrow().getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    private RuntimeException placeOrder(OrderRequestDTO request) {
        try {
            orderService.createOrder(request);
            return null;
        } catch (RuntimeException exception) {
            return exception;
        }
    }

    private OrderRequestDTO request(Product product, int quantity) {
        return new OrderRequestDTO(List.of(new OrderItemRequestDTO(product.getId(), quantity)));
    }

    private boolean hasCause(Throwable throwable, Class<? extends Throwable> expectedType) {
        for (Throwable cause = throwable; cause != null; cause = cause.getCause()) {
            if (expectedType.isInstance(cause)) {
                return true;
            }
        }
        return false;
    }

    private User createUser() {
        User user = userRepository.save(User.builder()
                .username("user-" + System.nanoTime())
                .password("password")
                .roles(UserRoles.ADMIN)
                .build());
        when(userService.getAuthenticatedUser()).thenReturn(user);
        return user;
    }

    private Product createProduct(String name, int stock) {
        Category category = categoryRepository.save(Category.builder().name(name + " category").build());
        return productRepository.save(Product.builder()
                .name(name)
                .price(BigDecimal.TEN)
                .category(category)
                .stock(stock)
                .build());
    }

    private OrderItem orderItem(Product product, int quantity) {
        return OrderItem.builder()
                .product(product)
                .price(product.getPrice())
                .quantity(quantity)
                .subtotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .build();
    }
}
