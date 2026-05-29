package io.github.mirvmir.frameworks.integration;

import io.github.mirvmir.domain.entities.order.Order;
import io.github.mirvmir.domain.entities.order.OrderItem;
import io.github.mirvmir.domain.entities.payment.Payment;
import io.github.mirvmir.domain.entities.payment.PaymentStatus;
import io.github.mirvmir.exception.business.OrderNotFoundException;
import io.github.mirvmir.exception.business.PaymentNotFoundException;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookUnitRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.OrderRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class DefaultPaymentWebhookService
        implements PaymentWebhookService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final BookUnitRepository bookUnitRepository;

    public DefaultPaymentWebhookService(PaymentRepository paymentRepository, OrderRepository orderRepository, BookUnitRepository bookUnitRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.bookUnitRepository = bookUnitRepository;
    }

    @Transactional
    @Override
    public void handlePaymentSucceeded(Long paymentId,
                                       String externalPaymentId,
                                       LocalDateTime paidAt) {
        Payment payment = paymentRepository.findByIdForUpdate(paymentId);

        if (payment == null) {
            throw new PaymentNotFoundException(
                    "Payment with ID " + paymentId + " not found."
            );
        }

        if (PaymentStatus.SUCCEEDED == payment.getStatus()) {
            return;
        }

        payment.markSucceeded(
                externalPaymentId,
                paidAt
        );

        paymentRepository.save(payment);

        markPayedByPayment(
                payment.getOrderId(),
                paidAt
        );
    }

    private void markPayedByPayment(Long orderId,
                                    LocalDateTime paidAt) {
        Order order = orderRepository.findByIdForUpdate(orderId);

        if (order == null) {
            throw new OrderNotFoundException(
                    "Order with ID " + orderId + " not found."
            );
        }

        if (order.isPayed()) {
            return;
        }

        order.markPayed(paidAt);

        List<Long> bookUnitIds = order.getItems()
                .stream()
                .map(OrderItem::getBookId)
                .filter(Objects::nonNull)
                .toList();

        bookUnitRepository.markSold(bookUnitIds);

        orderRepository.update(order);
    }
}