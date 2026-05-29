package io.github.mirvmir.frameworks.integration.outputs;

public record BankPayOutput(
        String paymentId,
        String status
) {
}
