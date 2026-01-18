package com.example.order.dtos;

import lombok.Data;

@Data
public class CartItemRequest {
    private String productId;
    private Integer quantity;
}
