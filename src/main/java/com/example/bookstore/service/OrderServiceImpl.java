package com.example.bookstore.service;

import com.example.bookstore.dto.order.OrderItemResponseDto;
import com.example.bookstore.dto.order.OrderRequestDto;
import com.example.bookstore.dto.order.OrderResponseDto;
import com.example.bookstore.dto.order.UpdateOrderStatusRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.exception.OrderProcessingException;
import com.example.bookstore.mapper.OrderItemMapper;
import com.example.bookstore.mapper.OrderMapper;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.OrderItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.Status;
import com.example.bookstore.repository.order.OrderRepository;
import com.example.bookstore.repository.orderitem.OrderItemRepository;
import com.example.bookstore.repository.shoppingcart.ShoppingCartRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderItemRepository orderItemRepository;

    @Override
    public Page<OrderResponseDto> getOrderHistory(Long id, Pageable pageable) {
        return orderRepository.findAllByUserId(id, pageable)
                .map(orderMapper::toResponseDto);
    }

    @Override
    public OrderResponseDto createOrder(Long userId, OrderRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findWithItemsAndBooksById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("ShoppingCart not found "
                            + "with user id: " + userId));
        if (shoppingCart.getCartItems().isEmpty()) {
            throw new OrderProcessingException("Couldn't process the order because "
                    + "the shopping cart is empty!");
        }
        Order order = initializeOrder(shoppingCart, requestDto);
        Order savedOrder = orderRepository.save(order);
        shoppingCart.getCartItems().clear();
        return orderMapper.toResponseDto(savedOrder);
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId,
                                              UpdateOrderStatusRequestDto requestDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: "
                        + orderId));
        order.setStatus(requestDto.getStatus());
        return orderMapper.toResponseDto(orderRepository.save(order));
    }

    @Override
    public Page<OrderItemResponseDto> getAllOrderItems(Long userId,
                                                       Long orderId,
                                                       Pageable pageable) {
        return orderItemRepository.findAllByOrder_User_IdAndOrder_Id(userId, orderId, pageable)
                 .map(orderItemMapper::toResponseDto);
    }

    @Override
    public OrderItemResponseDto getOrderItemById(Long userId, Long orderId, Long itemId) {
        OrderItem orderItem = orderItemRepository.findByIdAndOrderIdAndOrderUserId(itemId, orderId,
                        userId)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found with id: "
                        + itemId + " and order id: " + orderId));
        return orderItemMapper.toResponseDto(orderItem);
    }

    private Order initializeOrder(ShoppingCart shoppingCart, OrderRequestDto requestDto) {
        Order order = new Order();
        order.setUser(shoppingCart.getUser());
        order.setStatus(Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(toOrderItems(shoppingCart.getCartItems(), order));
        order.setTotalPrice(calculateTotalPrice(order.getOrderItems()));
        order.setShippingAddress(requestDto.getShippingAddress());
        return order;
    }

    private Set<OrderItem> toOrderItems(Set<CartItem> cartItems, Order order) {
        return cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = orderItemMapper.toModel(cartItem);
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toSet());
    }

    private BigDecimal calculateTotalPrice(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
