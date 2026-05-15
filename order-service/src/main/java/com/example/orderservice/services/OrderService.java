package com.example.orderservice.services;

import com.example.orderservice.clients.GigServiceClient;
import com.example.orderservice.dtos.CreateOrderRequest;
import com.example.orderservice.dtos.OrderDto;
import com.example.orderservice.entities.Order;
import com.example.orderservice.entities.enums.OrderStatus;
import com.example.orderservice.mappers.OrderMapper;
import com.example.orderservice.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final GigServiceClient gigServiceClient;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDto createOrder(CreateOrderRequest request, UUID clientId) {
        var gig = gigServiceClient.getGigById(request.getGigId());

        if (!gig.isAvailable()) {
            throw new IllegalStateException("The provider is currently busy on another order.");
        }

        if (gig.getProviderId().equals(clientId)) {
            throw new IllegalArgumentException("You cannot book your own gig!");
        }

        BigDecimal basePrice = gig.getPricing().getBasePrice();
        BigDecimal travelFee = BigDecimal.ZERO;

        if ("VISIT_CLIENT".equals(request.getServiceMode())) {
            if (request.getClientLatitude() == null || request.getClientLongitude() == null) {
                throw new IllegalArgumentException("Coordinates are required to calculate travel fee for home visits.");
            }
            travelFee = gigServiceClient.calculateVisitFee(request.getGigId(), request.getClientLatitude(), request.getClientLongitude());
        }

        BigDecimal totalPrice = basePrice.add(travelFee);

        Order order = Order.builder()
                .clientId(clientId)
                .providerId(gig.getProviderId())
                .gigId(gig.getId())
                .categoryId(gig.getCategory().getId())
                .serviceMode(request.getServiceMode())
                .basePrice(basePrice)
                .travelFee(travelFee)
                .totalPrice(totalPrice)
                .clientAddress(request.getClientAddress())
                .clientLatitude(request.getClientLatitude())
                .clientLongitude(request.getClientLongitude())
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto acceptOrder(UUID orderId, UUID providerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getProviderId().equals(providerId)) {
            throw new IllegalArgumentException("You are not authorized to accept this order.");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order is no longer pending. Current status: " + order.getStatus());
        }

        var gig = gigServiceClient.getGigById(order.getGigId());
        if (!gig.isAvailable()) {
            throw new IllegalStateException("You are currently busy on another active order!");
        }

        order.setStatus(OrderStatus.ACCEPTED);
        order.setUpdatedAt(LocalDateTime.now());

        gigServiceClient.updateAvailability(order.getGigId(), false);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto rejectOrder(UUID orderId, UUID providerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getProviderId().equals(providerId)) {
            throw new IllegalArgumentException("You are not authorized to reject this order.");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be rejected.");
        }

        order.setStatus(OrderStatus.REJECTED);
        order.setUpdatedAt(LocalDateTime.now());
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto completeOrder(UUID orderId, UUID providerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getProviderId().equals(providerId)) {
            throw new IllegalArgumentException("You are not authorized to complete this order.");
        }

        if (order.getStatus() != OrderStatus.ACCEPTED && order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only accepted/in-progress orders can be completed.");
        }

        order.setStatus(OrderStatus.COMPLETED);
        order.setUpdatedAt(LocalDateTime.now());

        gigServiceClient.updateAvailability(order.getGigId(), true);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto cancelOrder(UUID orderId, UUID userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getClientId().equals(userId) && !order.getProviderId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to cancel this order.");
        }

        OrderStatus previousStatus = order.getStatus();
        if (previousStatus == OrderStatus.COMPLETED || previousStatus == OrderStatus.REJECTED || previousStatus == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel a completed, rejected, or already cancelled order.");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        if (previousStatus == OrderStatus.ACCEPTED || previousStatus == OrderStatus.IN_PROGRESS) {
            gigServiceClient.updateAvailability(order.getGigId(), true);
        }

        return orderMapper.toDto(orderRepository.save(order));
    }

    public List<OrderDto> getClientOrders(UUID clientId) {
        return orderRepository.findByClientId(clientId).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<OrderDto> getProviderOrders(UUID providerId) {
        return orderRepository.findByProviderId(providerId).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }
}
