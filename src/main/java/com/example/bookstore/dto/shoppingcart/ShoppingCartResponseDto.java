package com.example.bookstore.dto.shoppingcart;

import com.example.bookstore.dto.cartitem.CartItemResponseDto;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class ShoppingCartResponseDto {
    private Long id;
    private Long userId;
    private Set<CartItemResponseDto> cartItems = new HashSet<>();
}
