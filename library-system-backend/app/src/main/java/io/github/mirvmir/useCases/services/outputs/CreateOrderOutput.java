package io.github.mirvmir.useCases.services.outputs;

public record CreateOrderOutput(
        Long orderId,
        Long paymentId,
        String orderStatus,
        String paymentStatus
) {
}