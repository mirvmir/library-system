package io.github.mirvmir.domain.entities.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payout {

    private Long id;
    private Long userId;
    private Long cardId;
    private String externalPayoutId;
    private BigDecimal price;
    private PayoutStatus status;
    private String description;
    private Long walletWithdrawalId;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public Payout(Long id,
                  Long userId,
                  Long cardId,
                  String externalPayoutId,
                  BigDecimal price,
                  PayoutStatus status,
                  String description,
                  Long walletWithdrawalId,
                  LocalDateTime createdAt,
                  LocalDateTime paidAt) {
        this.id = id;
        this.userId = userId;
        this.cardId = cardId;
        this.externalPayoutId = externalPayoutId;
        this.price = price;
        this.status = status;
        this.description = description;
        this.walletWithdrawalId = walletWithdrawalId;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
    }

    public static Payout create(
            Long userId,
            Long cardId,
            BigDecimal price,
            String description,
            Long walletWithdrawalId,
            LocalDateTime createdAt
    ) {
        return new Payout(
                null,
                userId,
                cardId,
                null,
                price,
                PayoutStatus.CREATED,
                description,
                walletWithdrawalId,
                createdAt,
                null
        );
    }

    public void markProcessing(String externalPayoutId) {
        if (PayoutStatus.CREATED != this.status) {
            throw new IllegalStateException("Payout already processed");
        }

        this.externalPayoutId = externalPayoutId;
        this.status = PayoutStatus.PROCESSING;
    }

    public void markSucceededFromWebhook(LocalDateTime paidAt) {
        if (PayoutStatus.SUCCEEDED == this.status) {
            return;
        }

        if (PayoutStatus.PROCESSING != this.status) {
            throw new IllegalStateException("Payout cannot be succeeded");
        }

        this.status = PayoutStatus.SUCCEEDED;
        this.paidAt = paidAt;
    }

    public void markFailedFromWebhook() {
        if (PayoutStatus.FAILED == this.status) {
            return;
        }

        if (PayoutStatus.PROCESSING != this.status) {
            throw new IllegalStateException("Payout cannot be failed");
        }

        this.status = PayoutStatus.FAILED;
    }

    public String getExternalPayoutId() {
        return externalPayoutId;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCardId() {
        return cardId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public PayoutStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public Long getWalletWithdrawalId() {
        return walletWithdrawalId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }
}