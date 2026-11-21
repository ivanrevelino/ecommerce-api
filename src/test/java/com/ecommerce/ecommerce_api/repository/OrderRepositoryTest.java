package com.ecommerce.ecommerce_api.repository;

import com.ecommerce.ecommerce_api.models.Order;
import com.ecommerce.ecommerce_api.models.User;
import com.ecommerce.ecommerce_api.models.enums.OrderStatus;
import com.ecommerce.ecommerce_api.models.enums.UserRoles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUserShouldReturnOnlyUsersOrders() {
        User user = createUser("owner");
        createOrder(user);
        createOrder(createUser("other"));

        assertThat(orderRepository.findByUser(user, PageRequest.of(0, 10)).getContent())
                .hasSize(1)
                .allMatch(order -> order.getUser().equals(user));
    }

    @Test
    void findByIdAndUserShouldRequireMatchingOwner() {
        User owner = createUser("owner");
        User other = createUser("other");
        Order order = createOrder(owner);

        assertThat(orderRepository.findByIdAndUser(order.getId(), owner)).contains(order);
        assertThat(orderRepository.findByIdAndUser(order.getId(), other)).isEmpty();
    }

    private User createUser(String username) {
        return userRepository.save(User.builder()
                .username(username)
                .password("password")
                .roles(UserRoles.USER)
                .build());
    }

    private Order createOrder(User user) {
        return orderRepository.save(Order.builder()
                .user(user)
                .total(BigDecimal.TEN)
                .status(OrderStatus.PENDING)
                .build());
    }
}
