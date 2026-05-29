package io.github.mirvmir.domain.entities.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private Long id;
    private Long userId;
    private String externalPaymentId;
    private BigDecimal price;
    private PaymentStatus status;
    private String description;
    private Long orderId;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public Payment(
            Long id,
            Long userId,
            String externalPaymentId,
            BigDecimal price,
            PaymentStatus status,
            String description,
            Long orderId,
            LocalDateTime createdAt,
            LocalDateTime paidAt
    ) {
        this.id = id;
        this.userId = userId;
        this.externalPaymentId = externalPaymentId;
        this.price = price;
        this.status = status;
        this.description = description;
        this.orderId = orderId;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
    }

    public static Payment create(
            Long userId,
            BigDecimal price,
            String description,
            Long orderId,
            LocalDateTime createdAt
    ) {
        return new Payment(
                null,
                userId,
                null,
                price,
                PaymentStatus.CREATED,
                description,
                orderId,
                createdAt,
                null
        );
    }

    public void expire() {
        this.status = PaymentStatus.EXPIRED;
    }

    public void cancel() {
        if (PaymentStatus.CANCELLED == this.status) {
            return;
        }

        if (PaymentStatus.CREATED != this.status) {
            throw new IllegalStateException("Payment cannot be cancelled");
        }

        this.status = PaymentStatus.CANCELLED;
    }

    public void markSucceeded(String externalPaymentId, LocalDateTime paidAt) {
        this.externalPaymentId = externalPaymentId;
        this.status = PaymentStatus.SUCCEEDED;
        this.paidAt = paidAt;
    }

    public void markFailed(String externalPaymentId) {
        this.externalPaymentId = externalPaymentId;
        this.status = PaymentStatus.FAILED;
    }

    public void markProcessing(String externalPaymentId) {
        if (PaymentStatus.CREATED != this.status) {
            throw new IllegalStateException("Payment already processed");
        }

        this.externalPaymentId = externalPaymentId;
        this.status = PaymentStatus.PROCESSING;
    }

    public void markSucceededFromWebhook(LocalDateTime paidAt) {
        if (PaymentStatus.SUCCEEDED == this.status) {
            return;
        }

        if (PaymentStatus.PROCESSING != this.status) {
            throw new IllegalStateException("Payment cannot be succeeded");
        }

        this.status = PaymentStatus.SUCCEEDED;
        this.paidAt = paidAt;
    }

    public void markFailedFromWebhook() {
        if (PaymentStatus.FAILED == this.status) {
            return;
        }

        if (PaymentStatus.PROCESSING != this.status) {
            throw new IllegalStateException("Payment cannot be failed");
        }

        this.status = PaymentStatus.FAILED;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getExternalPaymentId() {
        return externalPaymentId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public Long getOrderId() {
        return orderId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }
}