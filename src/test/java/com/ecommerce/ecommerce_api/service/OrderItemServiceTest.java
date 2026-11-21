package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_api.models.OrderItem;
import com.ecommerce.ecommerce_api.repository.OrderItemRepository;
import com.ecommerce.ecommerce_api.util.Generator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository repository;

    @InjectMocks
    private OrderItemService service;

    @Test
    void toDtoShouldCalculateSubtotal() {
        OrderItem item = Generator.orderItem();
        item.setId(1L);
        item.getProduct().setId(1L);

        assertThat(service.toDto(item)).isEqualTo(Generator.orderItemResponse());
    }

    @Test
    void findByIdShouldThrowWhenItemDoesNotExist() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order item not found");
    }

    @Test
    void saveAllShouldDelegateToRepository() {
        List<OrderItem> items = List.of(Generator.orderItem());
        when(repository.saveAll(items)).thenReturn(items);

        assertThat(service.saveAll(items)).isSameAs(items);
        verify(repository).saveAll(items);
    }
}
