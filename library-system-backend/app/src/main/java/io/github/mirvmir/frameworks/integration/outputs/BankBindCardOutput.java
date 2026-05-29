package io.github.mirvmir.frameworks.integration.outputs;

public record BankBindCardOutput(
        String bankCardId,
        String cardToken,
        String maskedPan,
        String last4,
        String paymentSystem,
        String status
) {
}
