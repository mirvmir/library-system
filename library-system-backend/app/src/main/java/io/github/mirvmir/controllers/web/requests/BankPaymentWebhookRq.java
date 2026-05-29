package io.github.mirvmir.controllers.web.requests;

public record BankPaymentWebhookRq(
        String externalPaymentId,
        String status,
        String failureReason
) {
}
