package io.github.mirvmir.domain.entities.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Refund {

    private Long id;
    private Long paymentId;
    private String externalRefundId;
    private BigDecimal price;
    private RefundStatus status;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime refundedAt;

    public Refund(Long id,
                  Long paymentId,
                  String externalRefundId,
                  BigDecimal price,
                  RefundStatus status,
                  String reason,
                  LocalDateTime createdAt,
                  LocalDateTime refundedAt) {
        this.id = id;
        this.paymentId = paymentId;
        this.externalRefundId = externalRefundId;
        this.price = price;
        this.status = status;
        this.reason = reason;
        this.createdAt = createdAt;
        this.refundedAt = refundedAt;
    }

    public static Refund create(
            Long paymentId,
            BigDecimal price,
            String reason,
            LocalDateTime now
    ) {
        return new Refund(
                null,
                paymentId,
                null,
                price,
                RefundStatus.CREATED,
                reason,
                now,
                null
        );
    }

    public static Refund load(
            Long id,
            Long paymentId,
            String externalRefundId,
            BigDecimal price,
            RefundStatus status,
            String reason,
            LocalDateTime createdAt,
            LocalDateTime refundedAt
    ) {
        return new Refund(
                id,
                paymentId,
                externalRefundId,
                price,
                status,
                reason,
                createdAt,
                refundedAt
        );
    }

    public void markProcessing(String externalRefundId) {
        if (RefundStatus.CREATED != this.status) {
            throw new IllegalStateException("Refund already processed");
        }

        this.externalRefundId = externalRefundId;
        this.status = RefundStatus.PROCESSING;
    }

    public void markSucceededFromWebhook(LocalDateTime refundedAt) {
        if (RefundStatus.SUCCEEDED == this.status) {
            return;
        }

        if (RefundStatus.PROCESSING != this.status) {
            throw new IllegalStateException("Refund cannot be succeeded");
        }

        this.status = RefundStatus.SUCCEEDED;
        this.refundedAt = refundedAt;
    }

    public void markFailedFromWebhook() {
        if (RefundStatus.FAILED == this.status) {
            return;
        }

        if (RefundStatus.PROCESSING != this.status) {
            throw new IllegalStateException("Refund cannot be failed");
        }

        this.status = RefundStatus.FAILED;
    }

    public boolean isSucceeded() {
        return RefundStatus.SUCCEEDED == this.status;
    }

    public Long getId() {
        return id;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public String getExternalRefundId() {
        return externalRefundId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }
}