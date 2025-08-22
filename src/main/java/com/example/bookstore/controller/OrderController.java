package com.example.bookstore.controller;

import com.example.bookstore.dto.order.OrderItemResponseDto;
import com.example.bookstore.dto.order.OrderRequestDto;
import com.example.bookstore.dto.order.OrderResponseDto;
import com.example.bookstore.dto.order.UpdateOrderStatusRequestDto;
import com.example.bookstore.security.AuthenticationService;
import com.example.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Orders management",
        description = "Endpoints for managing orders and order items")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final AuthenticationService authenticationService;
    private final OrderService orderService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get the order history of the user")
    @GetMapping
    public Page<OrderResponseDto> getOrderHistory(Authentication authentication,
                                                  Pageable pageable) {
        return orderService.getOrderHistory(authenticationService.getUserId(authentication),
                pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Place an order")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto createOrder(Authentication authentication,
                                           @RequestBody @Valid OrderRequestDto requestDto) {
        return orderService.createOrder(authenticationService.getUserId(authentication),
                requestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update the status of the order")
    @PatchMapping("/{orderId}")
    public OrderResponseDto updateOrderStatus(@PathVariable Long orderId,
                                              @RequestBody @Valid
                                              UpdateOrderStatusRequestDto requestDto) {
        return orderService.updateOrderStatus(orderId, requestDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get all order items of the order")
    @GetMapping("/{orderId}/items")
    public Page<OrderItemResponseDto> getAllOrderItems(Authentication authentication,
                                                       @PathVariable Long orderId,
                                                       Pageable pageable) {
        Long userId = authenticationService.getUserId(authentication);
        return orderService.getAllOrderItems(userId, orderId, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get order item of the order by id")
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemResponseDto getOrderItemById(Authentication authentication,
                                                       @PathVariable Long orderId,
                                                       @PathVariable Long itemId) {
        Long userId = authenticationService.getUserId(authentication);
        return orderService.getOrderItemById(userId, orderId, itemId);
    }
}
