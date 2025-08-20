package com.example.bookstore.service;

import com.example.bookstore.dto.cartitem.CartItemRequestDto;
import com.example.bookstore.dto.cartitem.UpdateCartItemDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.example.bookstore.model.User;

public interface ShoppingCartService {
    ShoppingCartResponseDto getCart(Long id);

    ShoppingCartResponseDto addItem(Long userId, CartItemRequestDto requestDto);

    ShoppingCartResponseDto updateQuantity(Long userId, Long cartItemId, UpdateCartItemDto itemDto);

    void deleteItem(Long userId, Long cartItemId);

    void createShoppingCart(User user);
}
