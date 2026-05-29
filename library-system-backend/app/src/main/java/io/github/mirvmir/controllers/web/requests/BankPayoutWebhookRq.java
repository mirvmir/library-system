package io.github.mirvmir.controllers.web.requests;

public record BankPayoutWebhookRq(
        String externalPayoutId,
        String status,
        String failureReason
) {
}
