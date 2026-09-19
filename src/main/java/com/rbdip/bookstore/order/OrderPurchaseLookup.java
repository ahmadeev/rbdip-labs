package com.rbdip.bookstore.order;

import com.rbdip.bookstore.purchase.PurchaseLookup;
import org.springframework.stereotype.Component;

@Component
public class OrderPurchaseLookup implements PurchaseLookup {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderPurchaseLookup(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public boolean hasPurchased() {
        return !orderRepository.findAll().isEmpty() && !orderItemRepository.findAll().isEmpty();
    }
}
