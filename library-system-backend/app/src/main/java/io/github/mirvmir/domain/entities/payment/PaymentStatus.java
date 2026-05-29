package io.github.mirvmir.domain.entities.payment;

public enum PaymentStatus {
    CREATED,
    CANCELLED,
    PROCESSING,
    SUCCEEDED,
    FAILED,
    EXPIRED
}