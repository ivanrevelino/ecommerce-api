package com.ecommerce.ecommerce_api.repository;

import com.ecommerce.ecommerce_api.models.Category;
import com.ecommerce.ecommerce_api.models.Order;
import com.ecommerce.ecommerce_api.models.OrderItem;
import com.ecommerce.ecommerce_api.models.Product;
import com.ecommerce.ecommerce_api.models.User;
import com.ecommerce.ecommerce_api.models.enums.OrderStatus;
import com.ecommerce.ecommerce_api.models.enums.UserRoles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindByIdShouldPersistOrderItem() {
        Category category = categoryRepository.save(Category.builder().name("Food").build());
        Product product = productRepository.save(Product.builder()
                .name("Banana")
                .price(BigDecimal.TEN)
                .category(category)
                .stock(5)
                .build());
        User user = userRepository.save(User.builder()
                .username("buyer")
                .password("password")
                .roles(UserRoles.USER)
                .build());
        Order order = orderRepository.save(Order.builder()
                .user(user)
                .total(BigDecimal.valueOf(20))
                .status(OrderStatus.PENDING)
                .build());
        OrderItem item = orderItemRepository.save(OrderItem.builder()
                .order(order)
                .product(product)
                .price(product.getPrice())
                .quantity(2)
                .subtotal(BigDecimal.valueOf(20))
                .build());

        assertThat(orderItemRepository.findById(item.getId()))
                .get()
                .usingRecursiveComparison()
                .isEqualTo(item);
    }
}
