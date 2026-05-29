package io.github.mirvmir.domain.entities.payment;

import java.time.LocalDateTime;

public class UserCard {

    private Long id;
    private Long userId;
    private String bankCardId;
    private String cardToken;
    private String maskedPan;
    private String last4;
    private String paymentSystem;
    private boolean active;
    private boolean defaultCard;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserCard(
            Long id,
            Long userId,
            String bankCardId,
            String cardToken,
            String maskedPan,
            String last4,
            String paymentSystem,
            boolean active,
            boolean defaultCard,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.bankCardId = bankCardId;
        this.cardToken = cardToken;
        this.maskedPan = maskedPan;
        this.last4 = last4;
        this.paymentSystem = paymentSystem;
        this.active = active;
        this.defaultCard = defaultCard;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserCard createBoundCard(
            Long userId,
            String bankCardId,
            String cardToken,
            String maskedPan,
            String last4,
            String paymentSystem,
            boolean defaultCard,
            LocalDateTime now
    ) {
        return new UserCard(
                null,
                userId,
                bankCardId,
                cardToken,
                maskedPan,
                last4,
                paymentSystem,
                true,
                defaultCard,
                now,
                now
        );
    }

    public void markAsDefault() {
        this.defaultCard = true;
    }

    public void unmarkAsDefault() {
        this.defaultCard = false;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getBankCardId() {
        return bankCardId;
    }

    public String getCardToken() {
        return cardToken;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public String getLast4() {
        return last4;
    }

    public String getPaymentSystem() {
        return paymentSystem;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isDefaultCard() {
        return defaultCard;
    }
}