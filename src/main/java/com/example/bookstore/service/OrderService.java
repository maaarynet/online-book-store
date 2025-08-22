package com.example.bookstore.service;

import com.example.bookstore.dto.order.OrderItemResponseDto;
import com.example.bookstore.dto.order.OrderRequestDto;
import com.example.bookstore.dto.order.OrderResponseDto;
import com.example.bookstore.dto.order.UpdateOrderStatusRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<OrderResponseDto> getOrderHistory(Long id, Pageable pageable);

    OrderResponseDto createOrder(Long userId, OrderRequestDto requestDto);

    OrderResponseDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto requestDto);

    Page<OrderItemResponseDto> getAllOrderItems(Long userId, Long orderId, Pageable pageable);

    OrderItemResponseDto getOrderItemById(Long userId, Long orderId, Long itemId);
}
