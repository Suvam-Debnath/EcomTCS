package com.example.order.Services;

import com.example.order.clients.ProductServiceClient;
import com.example.order.clients.UserServiceClient;
import com.example.order.dtos.CartItemRequest;
import com.example.order.dtos.ProductResponse;
import com.example.order.dtos.UserResponse;
import com.example.order.models.CartItem;
import com.example.order.repositories.CartItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CartService cartService;

    @DisplayName("Add to Cart - Success (New Cart Item)")
    @Test
    void test_When_Add_To_Cart_New_Item_Success() {

        CartItemRequest request = new CartItemRequest();
        request.setProductId("P1");
        request.setQuantity(2);


        when(productServiceClient.getProductDetails(anyString()))
                .thenReturn(getMockProductResponse());

        when(userServiceClient.getUserDetails(anyString()))
                .thenReturn(getMockUserResponse());

        when(cartItemRepository.findByUserIdAndProductId(anyString(), anyString()))
                .thenReturn(null);

        boolean result = cartService.addToCart("user1", request);

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        assertTrue(result);
    }

    @DisplayName("Add to Cart - Success (Existing Cart Item)")
    @Test
    void test_When_Add_To_Cart_Existing_Item_Success() {

        CartItemRequest request = new CartItemRequest();
        request.setProductId("P1");
        request.setQuantity(2);


        CartItem existingItem = new CartItem();
        existingItem.setUserId("user1");
        existingItem.setProductId("P1");
        existingItem.setQuantity(3);
        existingItem.setPrice(BigDecimal.valueOf(1000));

        when(productServiceClient.getProductDetails(anyString()))
                .thenReturn(getMockProductResponse());

        when(userServiceClient.getUserDetails(anyString()))
                .thenReturn(getMockUserResponse());

        when(cartItemRepository.findByUserIdAndProductId(anyString(), anyString()))
                .thenReturn(existingItem);

        boolean result = cartService.addToCart("user1", request);

        verify(cartItemRepository, times(1)).save(existingItem);
        assertEquals(5, existingItem.getQuantity());
        assertTrue(result);
    }

    @DisplayName("Add to Cart - Failure (Insufficient Stock)")
    @Test
    void test_When_Product_Out_Of_Stock_then_Fail() {

        CartItemRequest request = new CartItemRequest();
        request.setProductId("P1");
        request.setQuantity(2);

        ProductResponse productResponse = getMockProductResponse();
        productResponse.setStockQuantity(5);

        when(productServiceClient.getProductDetails(anyString()))
                .thenReturn(productResponse);

        boolean result = cartService.addToCart("user1", request);

        verify(cartItemRepository, never()).save(any());
        assertFalse(result);
    }

    @DisplayName("Add to Cart - Failure (User Not Found)")
    @Test
    void test_When_User_Not_Found_then_Fail() {

        CartItemRequest request = new CartItemRequest();
        request.setProductId("P1");
        request.setQuantity(2);


        when(productServiceClient.getProductDetails(anyString()))
                .thenReturn(getMockProductResponse());

        when(userServiceClient.getUserDetails(anyString()))
                .thenReturn(null);

        boolean result = cartService.addToCart("user1", request);

        verify(cartItemRepository, never()).save(any());
        assertFalse(result);
    }

    @DisplayName("Delete Item From Cart - Success")
    @Test
    void test_When_Delete_Item_Success() {

        CartItem cartItem = new CartItem();
        cartItem.setUserId("user1");
        cartItem.setProductId("P1");

        when(cartItemRepository.findByUserIdAndProductId(anyString(), anyString()))
                .thenReturn(cartItem);

        boolean result = cartService.deleteItemFromCart("user1", "P1");

        verify(cartItemRepository, times(1)).delete(cartItem);
        assertTrue(result);
    }

    @DisplayName("Delete Item From Cart - Failure")
    @Test
    void test_When_Delete_Item_Not_Found_then_Fail() {

        when(cartItemRepository.findByUserIdAndProductId(anyString(), anyString()))
                .thenReturn(null);

        boolean result = cartService.deleteItemFromCart("user1", "P1");

        verify(cartItemRepository, never()).delete(any());
        assertFalse(result);
    }

    @DisplayName("Get Cart Items - Success")
    @Test
    void test_When_Get_Cart_Success() {

        when(cartItemRepository.findByUserId(anyString()))
                .thenReturn(List.of(new CartItem(), new CartItem()));

        List<CartItem> cartItems = cartService.getCart("user1");

        verify(cartItemRepository, times(1)).findByUserId(anyString());
        assertEquals(2, cartItems.size());
    }

    @DisplayName("Clear Cart - Success")
    @Test
    void test_When_Clear_Cart_Success() {

        doNothing().when(cartItemRepository).deleteByUserId(anyString());

        cartService.clearCart("user1");

        verify(cartItemRepository, times(1)).deleteByUserId(anyString());
    }

    // 🔹 Mock helpers

    private ProductResponse getMockProductResponse() {
        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("iPhone");
        response.setPrice(BigDecimal.valueOf(1000));
        response.setStockQuantity(20);
        response.setActive(true);
        return response;
    }


    private UserResponse getMockUserResponse() {
        UserResponse response = new UserResponse();
        response.setId("user1");
        response.setEmail("user@test.com");
        return response;
    }

}
