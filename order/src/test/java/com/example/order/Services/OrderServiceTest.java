package com.example.order.Services;

import com.example.order.dtos.OrderResponse;
import com.example.order.models.CartItem;
import com.example.order.models.Order;
import com.example.order.models.OrderStatus;
import com.example.order.repositories.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CartService cartService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @DisplayName("Create Order - Success Scenario")
    @Test
    void test_When_Create_Order_Success() {

        CartItem item1 = new CartItem();
        item1.setId(1L);
        item1.setProductId("P1");
        item1.setQuantity(2);
        item1.setPrice(new BigDecimal("100"));

        CartItem item2 = new CartItem();
        item2.setId(2L);
        item2.setProductId("P2");
        item2.setQuantity(1);
        item2.setPrice(new BigDecimal("200"));

        List<CartItem> cartItems = List.of(item1, item2);


        when(cartService.getCart(anyString()))
                .thenReturn(cartItems);

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(1L);
                    order.setCreateAt(LocalDateTime.from(Instant.now()));
                    return order;
                });

        Optional<OrderResponse> response =
                orderService.createOrder("user123");

        verify(cartService).getCart(anyString());
        verify(orderRepository).save(any(Order.class));
        verify(cartService).clearCart(anyString());

        assertTrue(response.isPresent());
        assertEquals(OrderStatus.CONFIRMED, response.get().getStatus());
        assertEquals(new BigDecimal("300"), response.get().getTotalAmount());

    }

    @DisplayName("Create Order - Empty Cart Scenario")
    @Test
    void test_When_Cart_Is_Empty() {

        when(cartService.getCart(anyString()))
                .thenReturn(List.of());

        Optional<OrderResponse> response =
                orderService.createOrder("user123");

        verify(cartService).getCart(anyString());
        verify(orderRepository, never()).save(any());
        verify(cartService, never()).clearCart(anyString());

        assertTrue(response.isEmpty());
    }
}
