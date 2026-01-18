package com.example.order.Services;

import com.example.order.clients.ProductServiceClient;
import com.example.order.clients.UserServiceClient;
import com.example.order.dtos.CartItemRequest;
import com.example.order.dtos.ProductResponse;
import com.example.order.dtos.UserResponse;
import com.example.order.models.CartItem;
import com.example.order.repositories.CartItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductServiceClient productServiceClient;
    @Autowired
    private UserServiceClient userServiceClient;

    public boolean addToCart(String userId, CartItemRequest request) {
        //look for product
        ProductResponse productResponse = productServiceClient.getProductDetails(request.getProductId());
        if (productResponse==null || productResponse.getStockQuantity()<request.getQuantity()) return false;

        UserResponse userResponse = userServiceClient.getUserDetails(userId);
        if (userResponse == null)
            return false;

        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId());
        if (existingCartItem != null){
            //update the quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartItemRepository.save(existingCartItem);
        }else {
            //create new cart
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartItemRepository.save(cartItem);
        }
        return true;
    }

    public boolean deleteItemFromCart(String userId, String productId) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (cartItem != null){
            cartItemRepository.delete(cartItem);
            return true;
        }
        return false;
    }

    public List<CartItem> getCart(String userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }

}
