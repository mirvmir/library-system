package io.github.mirvmir.useCases.adapter.integration.payment.event;

import io.github.mirvmir.useCases.adapter.integration.payment.PaymentEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderExpiredEvent(
        Long orderId,
        Long customerId,
        BigDecimal totalPrice,
        List<String> bookIsbns,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        LocalDateTime expiredAt
) implements PaymentEvent {
}