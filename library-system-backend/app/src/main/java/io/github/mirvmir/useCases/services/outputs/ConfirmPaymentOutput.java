package io.github.mirvmir.useCases.services.outputs;

public record ConfirmPaymentOutput(
        Long paymentId,
        String status
) {
}
