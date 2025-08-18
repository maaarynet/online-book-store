package com.example.bookstore.service;

import com.example.bookstore.dto.cartitem.CartItemRequestDto;
import com.example.bookstore.dto.cartitem.UpdateCartItemDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.CartItemMapper;
import com.example.bookstore.mapper.ShoppingCartMapper;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.cartitem.CartItemRepository;
import com.example.bookstore.repository.shoppingcart.ShoppingCartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartResponseDto getCart(Long id) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find a shopping cart "
                        + "for user with id: " + id));
        return shoppingCartMapper.toResponseDto(shoppingCart);
    }

    @Override
    public ShoppingCartResponseDto addItem(Long id, CartItemRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find a shopping cart "
                        + "with id: " + id));
        CartItem cartItem = cartItemMapper.toModel(requestDto);
        cartItem.setShoppingCart(shoppingCart);
        shoppingCart.getCartItems().add(cartItem);
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toResponseDto(
                shoppingCartRepository.save(shoppingCart));

    }

    @Override
    public ShoppingCartResponseDto updateQuantity(Long id,
                                                  Long cartItemId,
                                                  UpdateCartItemDto itemDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find a shopping cart "
                        + "with id: " + id));
        CartItem cartItem = shoppingCart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst().orElseThrow(() -> new EntityNotFoundException("Couldn't find a cart "
                       + "item with id " + cartItemId + " in shopping cart with id: " + id));
        cartItemMapper.updateCartItemFromDto(cartItem, itemDto);
        return shoppingCartMapper.toResponseDto(shoppingCart);
    }

    @Override
    public void deleteItem(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartUserId(cartItemId, userId)
                .orElseThrow(()
                        -> new EntityNotFoundException("Could not find CartItem with id: "
                        + cartItemId + " and User id: " + userId));
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }
}
