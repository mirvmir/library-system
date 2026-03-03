package com.myapp.app.useCases.adapter.repository.interfaces;

import com.myapp.app.domain.entities.order.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository {
    Order save(Order order);
    void saveAll(List<Order> orders);
    Order findById(Long orderId);
    List<Order> findAll();
    Order update(Order order);
    BigDecimal calculateTotalEarningsByPeriod(LocalDateTime from, LocalDateTime to);
    long countCompletedByPeriod(LocalDateTime from, LocalDateTime to);
}