package com.example.orderservice.controllers;

import com.example.orderservice.dtos.CreateOrderRequest;
import com.example.orderservice.dtos.OrderDto;
import com.example.orderservice.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @RequestBody @Valid CreateOrderRequest request,
            @RequestHeader("X-user-Id") UUID clientId,
            UriComponentsBuilder uriBuilder
    ) {
        OrderDto orderDto = orderService.createOrder(request, clientId);
        var uri = uriBuilder.path("/orders/{id}").buildAndExpand(orderDto.getId()).toUri();
        return ResponseEntity.created(uri).body(orderDto);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<OrderDto> acceptOrder(
            @PathVariable UUID id,
            @RequestHeader("X-user-Id") UUID providerId
    ) {
        return ResponseEntity.ok(orderService.acceptOrder(id, providerId));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<OrderDto> rejectOrder(
            @PathVariable UUID id,
            @RequestHeader("X-user-Id") UUID providerId
    ) {
        return ResponseEntity.ok(orderService.rejectOrder(id, providerId));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<OrderDto> completeOrder(
            @PathVariable UUID id,
            @RequestHeader("X-user-Id") UUID providerId
    ) {
        return ResponseEntity.ok(orderService.completeOrder(id, providerId));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(
            @PathVariable UUID id,
            @RequestHeader("X-user-Id") UUID userId
    ) {
        return ResponseEntity.ok(orderService.cancelOrder(id, userId));
    }

    @GetMapping("/client")
    public ResponseEntity<List<OrderDto>> getClientOrders(@RequestHeader("X-user-Id") UUID clientId) {
        return ResponseEntity.ok(orderService.getClientOrders(clientId));
    }

    @GetMapping("/provider")
    public ResponseEntity<List<OrderDto>> getProviderOrders(@RequestHeader("X-user-Id") UUID providerId) {
        return ResponseEntity.ok(orderService.getProviderOrders(providerId));
    }
}
