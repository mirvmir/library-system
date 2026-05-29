package io.github.mirvmir.domain.entities.order;

public enum OrderStatus {
    CREATED,
    PAYMENT_PROCESSING,
    PAYED,
    COMPLETED,
    REFUND_REQUIRED,
    REFUNDED,
    CANCELLED,
    EXPIRED
}