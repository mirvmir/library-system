package io.github.mirvmir.frameworks.integration.outputs;

public record BankPayoutOutput(
        String externalPayoutId,
        String status
) {
}