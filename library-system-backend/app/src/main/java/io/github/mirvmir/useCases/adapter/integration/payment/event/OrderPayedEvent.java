package io.github.mirvmir.useCases.adapter.integration.payment.event;

import io.github.mirvmir.useCases.adapter.integration.payment.PaymentEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderPayedEvent(
        Long orderId,
        Long customerId,
        BigDecimal totalPrice,
        LocalDateTime payedAt
) implements PaymentEvent {
}