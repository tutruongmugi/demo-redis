package com.example.demo_redis.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItem {
    private String isbn;
    private Double price;
    private Long quantity;
}
