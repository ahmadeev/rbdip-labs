package com.rbdip.bookstore.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderController(
            OrderService orderService,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return Map.of("id", order.getId(), "status", order.getStatus());
    }

    @GetMapping("/orders")
    public List<Map<String, Object>> listOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> {
                    // N+1: отдельный запрос на позиции для каждого заказа вместо
                    // одного JOIN FETCH / batch-запроса. Цель для ЛР4.
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    return Map.<String, Object>of(
                            "id", order.getId(),
                            "customerFullName", order.getCustomer().getFullName(),
                            "status", order.getStatus(),
                            "items", items.stream()
                                    .map(i -> Map.of("productName", i.getProduct().getName(), "quantity", i.getQuantity()))
                                    .toList());
                })
                .toList();
    }
}
