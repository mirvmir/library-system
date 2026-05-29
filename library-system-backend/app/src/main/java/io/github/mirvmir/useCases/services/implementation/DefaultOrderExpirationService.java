package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.order.Order;
import io.github.mirvmir.domain.entities.order.OrderItem;
import io.github.mirvmir.domain.entities.payment.Payment;
import io.github.mirvmir.domain.entities.payment.PaymentStatus;
import io.github.mirvmir.event.EventPublisher;
import io.github.mirvmir.useCases.adapter.integration.payment.event.OrderExpiredEvent;
import io.github.mirvmir.useCases.adapter.repository.interfaces.OrderRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DefaultOrderExpirationService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    private final EventPublisher eventPublisher;

    private final Clock clock;

    public DefaultOrderExpirationService(OrderRepository orderRepository,
                                         PaymentRepository paymentRepository,
                                         EventPublisher eventPublisher,
                                         Clock clock) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void deleteExpiredOrders() {
        LocalDateTime now = LocalDateTime.now(clock);

        List<Order> orders = orderRepository.findExpiredForUpdate(now);

        if (orders.isEmpty()) {
            return;
        }

        for (Order order : orders) {
            expireOrder(order, now);
        }
    }

    private void expireOrder(Order order, LocalDateTime now) {
        if (order.isPayed()
                || order.isCancelled()
                || order.isRefunded()
                || order.isRefundRequired()) {
            return;
        }

        Payment payment = paymentRepository.findByOrderId(order.getId());

        if (payment != null && PaymentStatus.CREATED == payment.getStatus()) {
            payment.expire();
            paymentRepository.save(payment);
        }

        order.expire(now);
        orderRepository.update(order);

        eventPublisher.publishOrderExpired(
                new OrderExpiredEvent(
                        order.getId(),
                        order.getCustomerId(),
                        order.getTotalPrice(),
                        order.getItems()
                                .stream()
                                .map(OrderItem::getBookIsbn)
                                .toList(),
                        order.getCreatedAt(),
                        order.getExpiresAt(),
                        now
                )
        );
    }
}
