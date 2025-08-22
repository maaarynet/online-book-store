package com.example.bookstore.dto.order;

import com.example.bookstore.model.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class OrderResponseDto {
    private Long id;
    private Long userId;
    private Set<OrderItemResponseDto> orderItems = new HashSet<>();
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
    private Status status;
    private String shippingAddress;
}
