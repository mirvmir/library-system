package io.github.mirvmir.controllers.web.requests;

public record BankRefundWebhookRq(
        String externalRefundId,
        String status,
        String failureReason
) {
}
