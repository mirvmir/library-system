package io.github.mirvmir.frameworks.integration.outputs;

public record BankRefundOutput(
        String externalRefundId,
        String status
) {
}