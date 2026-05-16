package com.example.orderservice.services;

import com.example.orderservice.clients.GigServiceClient;
import com.example.orderservice.dtos.*;
import com.example.orderservice.entities.Order;
import com.example.orderservice.entities.enums.OrderStatus;
import com.example.orderservice.mappers.OrderMapper;
import com.example.orderservice.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private GigServiceClient gigServiceClient;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private UUID clientId;
    private UUID providerId;
    private UUID gigId;
    private GigDto gigDto;
    private CreateOrderRequest createOrderRequest;
    private Order orderEntity;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        providerId = UUID.randomUUID();
        gigId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        CategoryDto category = new CategoryDto();
        category.setId(categoryId);
        category.setName("Carpet Cleaning");

        ServicePricingDto pricing = new ServicePricingDto();
        pricing.setBasePrice(new BigDecimal("49.99"));
        pricing.setTravelFeePerKm(new BigDecimal("1.50"));
        pricing.setPriceType("FIXED");

        gigDto = new GigDto();
        gigDto.setId(gigId);
        gigDto.setTitle("Professional Carpet Cleaning");
        gigDto.setProviderId(providerId);
        gigDto.setPricing(pricing);
        gigDto.setCategory(category);
        gigDto.setAvailable(true);

        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setGigId(gigId);
        createOrderRequest.setServiceMode("IN_STORE");

        orderEntity = Order.builder()
                .id(UUID.randomUUID())
                .clientId(clientId)
                .providerId(providerId)
                .gigId(gigId)
                .categoryId(categoryId)
                .serviceMode("IN_STORE")
                .basePrice(new BigDecimal("49.99"))
                .travelFee(BigDecimal.ZERO)
                .totalPrice(new BigDecimal("49.99"))
                .status(OrderStatus.PENDING)
                .build();

        orderDto = new OrderDto();
        orderDto.setId(orderEntity.getId());
        orderDto.setClientId(clientId);
        orderDto.setProviderId(providerId);
        orderDto.setGigId(gigId);
        orderDto.setStatus("PENDING");
        orderDto.setTotalPrice(orderEntity.getTotalPrice());
    }

    @Test
    void createOrder_Success() {
        when(gigServiceClient.getGigById(gigId)).thenReturn(gigDto);
        when(orderRepository.save(any(Order.class))).thenReturn(orderEntity);
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);

        OrderDto result = orderService.createOrder(createOrderRequest, clientId);

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_ThrowsException_WhenGigNotAvailable() {
        gigDto.setAvailable(false);
        when(gigServiceClient.getGigById(gigId)).thenReturn(gigDto);

        Exception exception = assertThrows(IllegalStateException.class, () -> 
            orderService.createOrder(createOrderRequest, clientId)
        );

        assertTrue(exception.getMessage().contains("currently busy"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_ThrowsException_WhenBookingOwnGig() {
        when(gigServiceClient.getGigById(gigId)).thenReturn(gigDto);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            orderService.createOrder(createOrderRequest, providerId)
        );

        assertTrue(exception.getMessage().contains("cannot book your own gig"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_ThrowsException_WhenCoordinatesMissingForVisit() {
        createOrderRequest.setServiceMode("VISIT_CLIENT");
        when(gigServiceClient.getGigById(gigId)).thenReturn(gigDto);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            orderService.createOrder(createOrderRequest, clientId)
        );

        assertTrue(exception.getMessage().contains("Coordinates are required"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_Success_WithVisitClient_AndTravelFee() {
        createOrderRequest.setServiceMode("VISIT_CLIENT");
        createOrderRequest.setClientLatitude(37.7749);
        createOrderRequest.setClientLongitude(-122.4194);

        orderEntity.setServiceMode("VISIT_CLIENT");
        orderEntity.setTravelFee(new BigDecimal("15.00"));
        orderEntity.setTotalPrice(new BigDecimal("64.99"));

        orderDto.setTotalPrice(new BigDecimal("64.99"));

        when(gigServiceClient.getGigById(gigId)).thenReturn(gigDto);
        when(gigServiceClient.calculateVisitFee(gigId, 37.7749, -122.4194)).thenReturn(new BigDecimal("15.00"));
        when(orderRepository.save(any(Order.class))).thenReturn(orderEntity);
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);

        OrderDto result = orderService.createOrder(createOrderRequest, clientId);

        assertNotNull(result);
        assertEquals(new BigDecimal("64.99"), result.getTotalPrice());
        verify(gigServiceClient, times(1)).calculateVisitFee(gigId, 37.7749, -122.4194);
    }

    @Test
    void acceptOrder_Success() {
        when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
        when(gigServiceClient.getGigById(gigId)).thenReturn(gigDto);
        when(orderRepository.save(any(Order.class))).thenReturn(orderEntity);
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);

        OrderDto result = orderService.acceptOrder(orderEntity.getId(), providerId);

        assertNotNull(result);
        assertEquals(OrderStatus.ACCEPTED, orderEntity.getStatus());
        verify(gigServiceClient, times(1)).updateAvailability(gigId, false);
    }

    @Test
    void acceptOrder_ThrowsException_WhenNotAuthorized() {
        when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));

        assertThrows(IllegalArgumentException.class, () -> 
            orderService.acceptOrder(orderEntity.getId(), clientId)
        );
    }

    @Test
    void acceptOrder_ThrowsException_WhenNotPending() {
        orderEntity.setStatus(OrderStatus.ACCEPTED);
        when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));

        assertThrows(IllegalStateException.class, () -> 
            orderService.acceptOrder(orderEntity.getId(), providerId)
        );
    }

    @Test
    void rejectOrder_Success() {
        when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
        when(orderRepository.save(any(Order.class))).thenReturn(orderEntity);
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);

        OrderDto result = orderService.rejectOrder(orderEntity.getId(), providerId);

        assertNotNull(result);
        assertEquals(OrderStatus.REJECTED, orderEntity.getStatus());
    }

    @Test
    void completeOrder_Success() {
        orderEntity.setStatus(OrderStatus.ACCEPTED);
        when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
        when(orderRepository.save(any(Order.class))).thenReturn(orderEntity);
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);

        OrderDto result = orderService.completeOrder(orderEntity.getId(), providerId);

        assertNotNull(result);
        assertEquals(OrderStatus.COMPLETED, orderEntity.getStatus());
        verify(gigServiceClient, times(1)).updateAvailability(gigId, true);
    }

    @Test
    void completeOrder_ThrowsException_WhenStatusNotAcceptedOrInProgress() {
        orderEntity.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));

        assertThrows(IllegalStateException.class, () -> 
            orderService.completeOrder(orderEntity.getId(), providerId)
        );
    }

    @Test
    void cancelOrder_Success_FromAcceptedState_RestoresAvailability() {
        orderEntity.setStatus(OrderStatus.ACCEPTED);
        when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
        when(orderRepository.save(any(Order.class))).thenReturn(orderEntity);
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);

        OrderDto result = orderService.cancelOrder(orderEntity.getId(), clientId);

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, orderEntity.getStatus());
        verify(gigServiceClient, times(1)).updateAvailability(gigId, true);
    }
}
