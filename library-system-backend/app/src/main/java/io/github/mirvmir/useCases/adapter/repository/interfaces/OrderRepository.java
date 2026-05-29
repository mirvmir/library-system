package io.github.mirvmir.useCases.adapter.repository.interfaces;

import io.github.mirvmir.domain.entities.order.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository {
    Order save(Order order);
    void saveAll(List<Order> orders);
    Order update(Order order);
    Order findById(Long orderId);
    Order findByIdForUpdate(Long orderId);
    List<Order> findExpiredForUpdate(LocalDateTime now);
    List<Order> findAll();
    List<Order> findAllByUserId(Long userId);
    BigDecimal calculateTotalEarningsByPeriod(LocalDateTime from, LocalDateTime to);
    long countCompletedByPeriod(LocalDateTime from, LocalDateTime to);
}