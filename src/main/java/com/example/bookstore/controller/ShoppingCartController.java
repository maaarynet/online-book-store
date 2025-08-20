package com.example.bookstore.controller;

import com.example.bookstore.dto.cartitem.CartItemRequestDto;
import com.example.bookstore.dto.cartitem.UpdateCartItemDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.example.bookstore.security.AuthenticationService;
import com.example.bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final AuthenticationService authenticationService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get the shopping cart of the user")
    @GetMapping
    public ShoppingCartResponseDto getCart(Authentication authentication) {
        return shoppingCartService.getCart(authenticationService.getUserId(authentication));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Add a cart item to the shopping cart")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartResponseDto addItem(Authentication authentication,
                                       @RequestBody @Valid CartItemRequestDto requestItem) {
        return shoppingCartService.addItem(authenticationService.getUserId(authentication),
                requestItem);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Update a cart item quantity")
    @PutMapping("/items/{cartItemId}")
    public ShoppingCartResponseDto updateQuantity(Authentication authentication,
                                                  @PathVariable Long cartItemId,
                                                  @RequestBody @Valid UpdateCartItemDto itemDto) {
        return shoppingCartService.updateQuantity(authenticationService.getUserId(authentication),
                cartItemId, itemDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Delete item from the shopping cart")
    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(Authentication authentication, @PathVariable Long cartItemId) {
        shoppingCartService.deleteItem(authenticationService.getUserId(authentication), cartItemId);
    }

}
