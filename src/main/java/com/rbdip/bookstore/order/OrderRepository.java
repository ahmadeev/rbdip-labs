package com.rbdip.bookstore.order;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "customer")
    @Override
    List<Order> findAll();
}
